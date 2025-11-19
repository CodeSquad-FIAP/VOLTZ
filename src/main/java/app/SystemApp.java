package app;

import dao.*;
import model.*;
import db.OracleConnection;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

public class SystemApp {

    // DAOs
    private static CompanyDAO companyDAO;
    private static CryptoAssetDAO cryptoAssetDAO;
    private static WalletDAO walletDAO;
    private static MarketDAO marketDAO;
    private static TransactionDAO transactionDAO;
    private static CompanyCryptoAssetDAO companyCryptoAssetDAO;
    private static WalletCryptoAssetDAO walletCryptoAssetDAO;
    private static UserCompanyRelationDAO userCompanyRelationDAO;

    // Cache em memória
    private static List<User> userCache = new ArrayList<>();
    private static List<Company> companyCache = new ArrayList<>();
    private static List<CryptoAsset> cryptoAssetCache = new ArrayList<>();

    // Scanner global
    private static Scanner scanner = new Scanner(System.in);

    // Contadores de teste
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // Padrão Regex para Email Simples
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // ============================================================================
    // MAIN - MENU PRINCIPAL
    // ============================================================================

    public static void main(String[] args) {
        printHeader();
        initializeDAOs();
        loadDataToMemory();

        while (true) {
            try {
                System.out.println("\n" + "═".repeat(80));
                System.out.println("MENU PRINCIPAL");
                System.out.println("═".repeat(80));
                System.out.println("1. Executar Testes Automáticos (COM saída visual)");
                System.out.println("2. Executar Testes Automáticos (SEM saída visual - somente estatísticas)");
                System.out.println("3. CRUD Interativo - Gerenciar Dados");
                System.out.println("0. Sair");
                System.out.println("═".repeat(80));
                System.out.print("Escolha uma opção: ");

                int opcao = readInt();

                switch (opcao) {
                    case 1:
                        runAutomatedTests(true);
                        break;
                    case 2:
                        runAutomatedTests(false);
                        break;
                    case 3:
                        crudInteractiveMenu();
                        break;
                    case 0:
                        System.out.println("\n👋 Encerrando sistema...");
                        OracleConnection.closeConnection();
                        System.out.println("✅ Sistema finalizado com sucesso!");
                        System.exit(0);
                    default:
                        System.out.println("❌ Opção inválida! Tente novamente.");
                }

            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
                scanner.nextLine(); // Limpar buffer
            }
        }
    }

    // ============================================================================
    // CRUD INTERATIVO - MENU
    // ============================================================================

    private static void crudInteractiveMenu() {
        while (true) {
            System.out.println("\n" + "═".repeat(80));
            System.out.println("CRUD INTERATIVO - ESCOLHA UMA ENTIDADE");
            System.out.println("═".repeat(80));
            System.out.println("1. Usuários (Users)");
            System.out.println("2. Empresas (Companies)");
            System.out.println("3. Ativos Cripto (CryptoAssets)");
            System.out.println("4. Carteiras (Wallets)");
            System.out.println("5. Preços de Mercado (Market)");
            System.out.println("6. Transações (Transactions)");
            System.out.println("7. Relacionamentos Usuário-Empresa");
            System.out.println("8. Alocação de Ativos para Empresas");
            System.out.println("9. Ativos em Carteiras");
            System.out.println("10. Recarregar Cache do DB");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.println("═".repeat(80));
            System.out.print("Escolha uma opção: ");

            int opcao = readInt();

            switch (opcao) {
                case 1: crudUsers(); break;
                case 2: crudCompanies(); break;
                case 3: crudCryptoAssets(); break;
                case 4: crudWallets(); break;
                case 5: crudMarket(); break;
                case 6: crudTransactions(); break;
                case 7: crudUserCompanyRelation(); break;
                case 8: crudCompanyCryptoAsset(); break;
                case 9: crudWalletCryptoAsset(); break;
                case 10: loadDataToMemory(); break;
                case 0: return;
                default: System.out.println("❌ Opção inválida!");
            }
        }
    }

    // ============================================================================
    // CRUD - USERS
    // ============================================================================

    private static void crudUsers() {
        while (true) {
            System.out.println("\n" + "─".repeat(80));
            System.out.println("CRUD - USUÁRIOS");
            System.out.println("─".repeat(80));
            System.out.println("1. Listar todos os usuários");
            System.out.println("2. Buscar usuário por ID");
            System.out.println("3. Criar novo usuário");
            System.out.println("4. Atualizar usuário");
            System.out.println("5. Deletar usuário");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        System.out.println("\n👥 Total: " + userCache.size() + " usuário(s) em cache");
                        if (userCache.isEmpty()) {
                            System.out.println("Cache vazio.");
                        } else {
                            userCache.forEach(User::showInfo);
                        }
                        break;

                    case 2:
                        System.out.print("Digite o ID do usuário: ");
                        int id = readPositiveInt(); // Validação: ID positivo
                        User user = userCache.stream()
                                .filter(u -> u.getId() == id)
                                .findFirst()
                                .orElse(null);

                        if (user != null) {
                            user.showInfo();
                        } else {
                            System.out.println("❌ Usuário não encontrado no cache!");
                        }
                        break;

                    case 3:
                        System.out.println("\n=== CRIAR NOVO USUÁRIO ===");
                        
                        // Validação: Nome não vazio
                        String nome = readStringNotEmpty("Nome");

                        // Validação: Email válido
                        String email;
                        while (true) {
                            System.out.print("Email: ");
                            email = scanner.nextLine().trim();
                            if (isValidEmail(email)) break;
                            System.out.println("❌ Email inválido! Use o formato exemplo@dominio.com");
                        }

                        // Validação: Senha não vazia
                        String senha = readStringNotEmpty("Senha");

                        int novoId = getNextUserIdFromCache();
                        User novoUser = new User(nome, novoId, email, senha);
                        novoUser.insert();
                        userCache.add(novoUser);
                        System.out.println("✅ Usuário criado e adicionado ao cache.");
                        break;

                    case 4:
                        System.out.print("Digite o ID do usuário a atualizar: ");
                        int updateId = readPositiveInt();
                        User userToUpdate = userCache.stream()
                                .filter(u -> u.getId() == updateId)
                                .findFirst()
                                .orElse(null);

                        if (userToUpdate != null) {
                            System.out.println("Dados atuais:");
                            userToUpdate.showInfo();

                            System.out.print("\nNovo nome (Enter para manter atual): ");
                            String newName = scanner.nextLine();
                            if (!newName.trim().isEmpty()) {
                                userToUpdate.setName(newName.trim());
                            }

                            System.out.print("Novo email (Enter para manter atual): ");
                            String newEmail = scanner.nextLine();
                            if (!newEmail.trim().isEmpty()) {
                                if (isValidEmail(newEmail.trim())) {
                                    userToUpdate.setEmail(newEmail.trim());
                                } else {
                                    System.out.println("❌ Email inválido ignorado. Mantendo anterior.");
                                }
                            }

                            System.out.print("Nova senha (Enter para manter atual): ");
                            String newPassword = scanner.nextLine();
                            if (!newPassword.trim().isEmpty()) {
                                userToUpdate.setPassword(newPassword.trim());
                            }

                            userToUpdate.update();
                            System.out.println("✅ Usuário atualizado no BD e cache.");
                        } else {
                            System.out.println("❌ Usuário não encontrado!");
                        }
                        break;

                    case 5:
                        System.out.print("Digite o ID do usuário a deletar: ");
                        int deleteId = readPositiveInt();
                        User userToDelete = userCache.stream()
                                .filter(u -> u.getId() == deleteId)
                                .findFirst()
                                .orElse(null);

                        if (userToDelete != null) {
                            System.out.println("Confirma exclusão de:");
                            userToDelete.showInfo();
                            System.out.print("Digite 'SIM' para confirmar: ");
                            String confirmacao = scanner.nextLine();

                            if ("SIM".equalsIgnoreCase(confirmacao.trim())) {
                                userToDelete.delete();
                                userCache.remove(userToDelete);
                                System.out.println("✅ Usuário deletado do DB e cache.");
                            } else {
                                System.out.println("❌ Exclusão cancelada.");
                            }
                        } else {
                            System.out.println("❌ Usuário não encontrado!");
                        }
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("❌ Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    // ============================================================================
    // CRUD - COMPANIES
    // ============================================================================

    private static void crudCompanies() {
        while (true) {
            System.out.println("\n" + "─".repeat(80));
            System.out.println("CRUD - EMPRESAS");
            System.out.println("─".repeat(80));
            System.out.println("1. Listar todas as empresas");
            System.out.println("2. Buscar empresa por ID");
            System.out.println("3. Criar nova empresa");
            System.out.println("4. Atualizar empresa");
            System.out.println("5. Deletar empresa");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        System.out.println("\n📊 Total: " + companyCache.size() + " empresa(s) em cache");
                        companyCache.forEach(Company::showInfo);
                        break;

                    case 2:
                        System.out.print("Digite o ID da empresa: ");
                        int id = readPositiveInt();
                        Company company = companyCache.stream()
                                .filter(c -> c.getId() == id)
                                .findFirst()
                                .orElse(null);

                        if (company != null) {
                            company.showInfo();
                        } else {
                            System.out.println("❌ Empresa não encontrada no cache!");
                        }
                        break;

                    case 3:
                        System.out.println("\n=== CRIAR NOVA EMPRESA ===");
                        String nome = readStringNotEmpty("Nome da Empresa");

                        System.out.print("ID (número): ");
                        int novoId = readPositiveInt();

                        String identificador = readStringNotEmpty("Identificador (CNPJ/Código)");

                        Company novaCompany = new Company(nome, novoId, identificador);
                        companyDAO.insert(novaCompany);
                        companyCache.add(novaCompany);
                        System.out.println("✅ Empresa criada e adicionada ao cache.");
                        break;

                    case 4:
                        System.out.print("Digite o ID da empresa a atualizar: ");
                        int updateId = readPositiveInt();
                        Company companyToUpdate = companyCache.stream()
                                .filter(c -> c.getId() == updateId)
                                .findFirst()
                                .orElse(null);

                        if (companyToUpdate != null) {
                            System.out.println("Dados atuais:");
                            companyToUpdate.showInfo();

                            System.out.print("\nNovo nome (Enter para manter): ");
                            String newName = scanner.nextLine();
                            if (!newName.trim().isEmpty()) {
                                companyToUpdate.setName(newName.trim());
                            }

                            System.out.print("Novo identificador (Enter para manter): ");
                            String newId = scanner.nextLine();
                            if (!newId.trim().isEmpty()) {
                                companyToUpdate.setIdentifier(newId.trim());
                            }

                            companyDAO.update(companyToUpdate);
                            System.out.println("✅ Empresa atualizada no DB e cache.");
                        } else {
                            System.out.println("❌ Empresa não encontrada!");
                        }
                        break;

                    case 5:
                        System.out.print("Digite o ID da empresa a deletar: ");
                        int deleteId = readPositiveInt();
                        Company companyToDelete = companyCache.stream()
                                .filter(c -> c.getId() == deleteId)
                                .findFirst()
                                .orElse(null);

                        if (companyToDelete != null) {
                            System.out.println("Confirma exclusão de:");
                            companyToDelete.showInfo();
                            System.out.print("Digite 'SIM' para confirmar: ");
                            String confirmacao = scanner.nextLine();

                            if ("SIM".equalsIgnoreCase(confirmacao.trim())) {
                                companyDAO.delete(deleteId);
                                companyCache.remove(companyToDelete);
                                System.out.println("✅ Empresa deletada do DB e cache.");
                            } else {
                                System.out.println("❌ Exclusão cancelada.");
                            }
                        } else {
                            System.out.println("❌ Empresa não encontrada!");
                        }
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("❌ Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    // ============================================================================
    // CRUD - CRYPTOASSETS
    // ============================================================================

    private static void crudCryptoAssets() {
        while (true) {
            System.out.println("\n" + "─".repeat(80));
            System.out.println("CRUD - ATIVOS CRIPTO");
            System.out.println("─".repeat(80));
            System.out.println("1. Listar todos os ativos");
            System.out.println("2. Buscar ativo por ID");
            System.out.println("3. Criar novo ativo");
            System.out.println("4. Atualizar ativo");
            System.out.println("5. Deletar ativo");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        System.out.println("\n💎 Total: " + cryptoAssetCache.size() + " ativo(s) em cache");
                        for (CryptoAsset asset : cryptoAssetCache) {
                            System.out.printf("- %s (%s) | Qty: %.4f | Price: $%.2f%n",
                                    asset.getName(), asset.getSymbol(), asset.getQuantity(), asset.getPrice());
                        }
                        break;

                    case 2:
                        System.out.print("Digite o ID do ativo: ");
                        int id = readPositiveInt();
                        CryptoAsset asset = cryptoAssetCache.stream()
                                .filter(a -> a.getId() == id)
                                .findFirst()
                                .orElse(null);

                        if (asset != null) {
                            System.out.printf("%s (%s) | Qty: %.4f | Price: $%.2f%n",
                                    asset.getName(), asset.getSymbol(), asset.getQuantity(), asset.getPrice());
                        } else {
                            System.out.println("❌ Ativo não encontrado no cache!");
                        }
                        break;

                    case 3:
                        System.out.println("\n=== CRIAR NOVO ATIVO ===");
                        String nome = readStringNotEmpty("Nome");
                        String simbolo;

                        while (true) {
                            simbolo = readStringNotEmpty("Símbolo (ex: BTC)");
                            if (simbolo.length() <= 10) break;
                            System.out.println("❌ Símbolo muito longo! Máximo 10 caracteres.");
                        }

                        System.out.print("Quantidade inicial: ");
                        double quantidade = readPositiveDouble();

                        System.out.print("Preço: ");
                        double preco = readPositiveDouble();

                        CryptoAsset novoAsset = new CryptoAsset(nome, simbolo.toUpperCase(), quantidade, preco);
                        cryptoAssetDAO.insert(novoAsset);
                        cryptoAssetCache.add(novoAsset);
                        System.out.println("✅ Ativo criado e adicionado ao cache.");
                        break;

                    case 4:
                        System.out.print("Digite o símbolo do ativo a atualizar (ex: BTC): ");
                        String symbolToUpdate = scanner.nextLine().trim();

                        CryptoAsset assetToUpdate = cryptoAssetCache.stream()
                                .filter(a -> a.getSymbol().equalsIgnoreCase(symbolToUpdate))
                                .findFirst()
                                .orElse(null);

                        if (assetToUpdate != null) {
                            System.out.printf("Dados atuais: %s (%s) | Qty: %.4f | Price: $%.2f%n",
                                    assetToUpdate.getName(), assetToUpdate.getSymbol(),
                                    assetToUpdate.getQuantity(), assetToUpdate.getPrice());

                            System.out.print("\nNovo nome (Enter para manter): ");
                            String newName = scanner.nextLine();
                            if (!newName.trim().isEmpty()) assetToUpdate.setName(newName.trim());

                            System.out.print("Nova quantidade (Enter para manter): ");
                            String qtyStr = scanner.nextLine();
                            if (!qtyStr.trim().isEmpty()) {
                                try {
                                    double val = Double.parseDouble(qtyStr);
                                    if (val >= 0) assetToUpdate.setQuantity(val);
                                    else System.out.println("❌ Valor negativo ignorado.");
                                } catch (NumberFormatException e) {
                                    System.out.println("❌ Número inválido ignorado.");
                                }
                            }

                            System.out.print("Novo preço (Enter para manter): ");
                            String priceStr = scanner.nextLine();
                            if (!priceStr.trim().isEmpty()) {
                                try {
                                    double val = Double.parseDouble(priceStr);
                                    if (val >= 0) assetToUpdate.setPrice(val);
                                    else System.out.println("❌ Valor negativo ignorado.");
                                } catch (NumberFormatException e) {
                                    System.out.println("❌ Número inválido ignorado.");
                                }
                            }

                            cryptoAssetDAO.update(assetToUpdate, symbolToUpdate);
                            System.out.println("✅ Ativo atualizado no DB e cache.");
                        } else {
                            System.out.println("❌ Ativo não encontrado!");
                        }
                        break;

                    case 5:
                        System.out.print("Digite o símbolo do ativo a deletar: ");
                        String symbolToDelete = scanner.nextLine().trim();

                        System.out.print("Digite 'SIM' para confirmar: ");
                        String confirmacao = scanner.nextLine();

                        if ("SIM".equalsIgnoreCase(confirmacao.trim())) {
                            cryptoAssetDAO.delete(symbolToDelete);
                            cryptoAssetCache.removeIf(a -> a.getSymbol().equalsIgnoreCase(symbolToDelete));
                            System.out.println("✅ Ativo deletado no DB e cache.");
                        } else {
                            System.out.println("❌ Exclusão cancelada.");
                        }
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("❌ Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    // ============================================================================
    // CRUD - WALLETS
    // ============================================================================

    private static void crudWallets() {
        while (true) {
            System.out.println("\n" + "─".repeat(80));
            System.out.println("CRUD - CARTEIRAS");
            System.out.println("─".repeat(80));
            System.out.println("1. Listar todas as carteiras");
            System.out.println("2. Buscar carteira por ID");
            System.out.println("3. Criar nova carteira");
            System.out.println("4. Atualizar carteira");
            System.out.println("5. Deletar carteira");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        List<Wallet> wallets = walletDAO.findAll();
                        System.out.println("\n👛 Total: " + wallets.size() + " carteira(s)");
                        for (Wallet w : wallets) {
                            System.out.printf("- ID: %d | User ID: %d | Nome: %s%n",
                                    w.getId(), w.getUserId(), w.getName());
                        }
                        break;

                    case 2:
                        System.out.print("Digite o ID da carteira: ");
                        int id = readPositiveInt();
                        Wallet wallet = walletDAO.findById(id);
                        if (wallet != null) {
                            System.out.printf("ID: %d | User ID: %d | Nome: %s%n",
                                    wallet.getId(), wallet.getUserId(), wallet.getName());
                        } else {
                            System.out.println("❌ Carteira não encontrada!");
                        }
                        break;

                    case 3:
                        System.out.println("\n=== CRIAR NOVA CARTEIRA ===");
                        System.out.print("ID da carteira: ");
                        int novoId = readPositiveInt();

                        System.out.print("ID do usuário proprietário: ");
                        int userId = readPositiveInt();

                        String nome = readStringNotEmpty("Nome da carteira");

                        Wallet novaWallet = new Wallet(novoId, userId, nome);
                        walletDAO.insert(novaWallet);
                        break;

                    case 4:
                        System.out.print("Digite o ID da carteira a atualizar: ");
                        int updateId = readPositiveInt();
                        Wallet walletToUpdate = walletDAO.findById(updateId);

                        if (walletToUpdate != null) {
                            System.out.printf("Dados atuais: ID: %d | User ID: %d%n",
                                    walletToUpdate.getId(), walletToUpdate.getUserId());

                            System.out.print("Novo ID do usuário (Enter para manter): ");
                            String userIdStr = scanner.nextLine();
                            int newUserId = userIdStr.trim().isEmpty() ?
                                    walletToUpdate.getUserId() : Integer.parseInt(userIdStr);
                            
                            if (newUserId < 0) {
                                System.out.println("❌ ID inválido. Mantendo anterior.");
                                newUserId = walletToUpdate.getUserId();
                            }

                            Wallet updated = new Wallet(updateId, newUserId, "Wallet " + updateId);
                            walletDAO.update(updated);
                        } else {
                            System.out.println("❌ Carteira não encontrada!");
                        }
                        break;

                    case 5:
                        System.out.print("Digite o ID da carteira a deletar: ");
                        int deleteId = readPositiveInt();

                        System.out.print("Digite 'SIM' para confirmar: ");
                        String confirmacao = scanner.nextLine();

                        if ("SIM".equalsIgnoreCase(confirmacao.trim())) {
                            walletDAO.delete(deleteId);
                        } else {
                            System.out.println("❌ Exclusão cancelada.");
                        }
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("❌ Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    // ============================================================================
    // CRUD - MARKET PRICES
    // ============================================================================

    private static void crudMarket() {
        while (true) {
            System.out.println("\n" + "─".repeat(80));
            System.out.println("CRUD - PREÇOS DE MERCADO");
            System.out.println("─".repeat(80));
            System.out.println("1. Listar todos os preços");
            System.out.println("2. Buscar preço por símbolo");
            System.out.println("3. Criar novo preço");
            System.out.println("4. Atualizar preço");
            System.out.println("5. Deletar preço");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        Map<String, Double> prices = marketDAO.getAllPrices();
                        System.out.println("\n💹 Total: " + prices.size() + " preço(s)");
                        prices.forEach((symbol, price) ->
                                System.out.printf("- %s: $%,.2f%n", symbol, price));
                        break;

                    case 2:
                        System.out.print("Digite o símbolo (ex: BTC): ");
                        String symbol = scanner.nextLine().toUpperCase().trim();
                        Double price = marketDAO.getPrice(symbol);
                        if (price != null) {
                            System.out.printf("💰 %s: $%,.2f%n", symbol, price);
                        } else {
                            System.out.println("❌ Preço não encontrado!");
                        }
                        break;

                    case 3:
                        System.out.println("\n=== CRIAR NOVO PREÇO ===");
                        String newSymbol = readStringNotEmpty("Símbolo").toUpperCase();

                        if (marketDAO.getPrice(newSymbol) != null) {
                            System.out.println("❌ Erro: Símbolo já existe. Use a Opção 4 para atualizar.");
                            break;
                        }

                        System.out.print("Preço: $");
                        double newPrice = readPositiveDouble();

                        marketDAO.save(newSymbol, newPrice);
                        break;

                    case 4:
                        System.out.println("\n=== ATUALIZAR PREÇO ===");
                        String updateSymbol = readStringNotEmpty("Símbolo").toUpperCase();

                        Double currentPrice = marketDAO.getPrice(updateSymbol);
                        if (currentPrice == null) {
                            System.out.println("❌ Erro: Símbolo não encontrado. Use a Opção 3 para criar.");
                            break;
                        }

                        System.out.printf("Preço atual de %s: $%,.2f%n", updateSymbol, currentPrice);
                        System.out.print("Novo preço: $");
                        double updatePrice = readPositiveDouble();

                        marketDAO.save(updateSymbol, updatePrice);
                        break;

                    case 5:
                        System.out.print("Digite o símbolo a deletar: ");
                        String deleteSymbol = scanner.nextLine().toUpperCase().trim();

                        System.out.print("Digite 'SIM' para confirmar: ");
                        String confirmacao = scanner.nextLine();

                        if ("SIM".equalsIgnoreCase(confirmacao.trim())) {
                            marketDAO.delete(deleteSymbol);
                        } else {
                            System.out.println("❌ Exclusão cancelada.");
                        }
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("❌ Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    // ============================================================================
    // CRUD - TRANSACTIONS
    // ============================================================================

    private static void crudTransactions() {
        while (true) {
            System.out.println("\n" + "─".repeat(80));
            System.out.println("CRUD - TRANSAÇÕES");
            System.out.println("─".repeat(80));
            System.out.println("1. Listar transações por usuário");
            System.out.println("2. Buscar transação por ID");
            System.out.println("3. Criar nova transação");
            System.out.println("4. Atualizar transação");
            System.out.println("5. Deletar transação");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("Digite o ID do usuário: ");
                        int userId = readPositiveInt();
                        List<Transaction> transactions = transactionDAO.findByUserId(userId);
                        System.out.println("\n📝 Total: " + transactions.size() + " transação(ões)");
                        transactions.forEach(Transaction::showTransaction);
                        break;

                    case 2:
                        System.out.print("Digite o ID da transação: ");
                        int findId = readPositiveInt();

                        Transaction tx = transactionDAO.findById(findId);
                        if (tx != null) {
                            tx.showTransaction();
                        } else {
                            System.out.println("❌ Transação não encontrada!");
                        }
                        break;

                    case 3:
                        System.out.println("\n=== CRIAR NOVA TRANSAÇÃO ===");
                        System.out.print("ID do usuário: ");
                        int newUserId = readPositiveInt();

                        System.out.print("ID do ativo cripto: ");
                        int assetId = readPositiveInt();

                        CryptoAsset asset = cryptoAssetDAO.findById(assetId);
                        if (asset == null) {
                            System.out.println("❌ Ativo não encontrado!");
                            break;
                        }

                        System.out.print("Quantidade: ");
                        double amount = readPositiveDouble();
                        if (amount <= 0) {
                            System.out.println("❌ A quantidade deve ser maior que zero!");
                            break;
                        }

                        System.out.print("Tipo (BUY/SELL): ");
                        String type = scanner.nextLine().toUpperCase().trim();

                        if (!type.equals("BUY") && !type.equals("SELL")) {
                            System.out.println("❌ Tipo inválido! Use BUY ou SELL.");
                            break;
                        }

                        Transaction newTransaction = new Transaction(asset, amount, type);
                        transactionDAO.insert(newTransaction, newUserId, assetId);
                        break;

                    case 4:
                        System.out.print("Digite o ID da transação a atualizar: ");
                        int updateId = readPositiveInt();

                        Transaction txToUpdate = transactionDAO.findById(updateId);

                        if (txToUpdate == null) {
                            System.out.println("❌ Transação não encontrada!");
                            break;
                        }

                        System.out.println("Dados atuais:");
                        txToUpdate.showTransaction();

                        System.out.print("Nova quantidade (Enter para manter): ");
                        String amountStr = scanner.nextLine();
                        double newAmount = amountStr.trim().isEmpty() ?
                                txToUpdate.getAmount() : Double.parseDouble(amountStr);
                        
                        if (newAmount <= 0) {
                             System.out.println("❌ Quantidade deve ser positiva. Mantendo valor anterior.");
                             newAmount = txToUpdate.getAmount();
                        }

                        System.out.print("Novo tipo (BUY/SELL) (Enter para manter): ");
                        String newType = scanner.nextLine().toUpperCase().trim();
                        if (newType.isEmpty()) {
                            newType = txToUpdate.getType();
                        }

                        if (!newType.equals("BUY") && !newType.equals("SELL")) {
                            System.out.println("❌ Tipo inválido! Mantendo valor anterior.");
                            newType = txToUpdate.getType();
                        }

                        txToUpdate.setAmount(newAmount);
                        txToUpdate.setType(newType);
                        transactionDAO.update(txToUpdate);

                        System.out.println("✅ Transação atualizada!");
                        break;

                    case 5:
                        System.out.print("Digite o ID da transação a deletar: ");
                        int deleteId = readPositiveInt();

                        System.out.print("Digite 'SIM' para confirmar: ");
                        String confirmacao = scanner.nextLine();

                        if ("SIM".equalsIgnoreCase(confirmacao.trim())) {
                            transactionDAO.delete(deleteId);
                        } else {
                            System.out.println("❌ Exclusão cancelada.");
                        }
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("❌ Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    // ============================================================================
    // CRUD - USER COMPANY RELATION
    // ============================================================================

    private static void crudUserCompanyRelation() {
        while (true) {
            System.out.println("\n" + "─".repeat(80));
            System.out.println("CRUD - RELACIONAMENTOS USUÁRIO-EMPRESA");
            System.out.println("─".repeat(80));
            System.out.println("1. Listar usuários de uma empresa");
            System.out.println("2. Buscar relacionamento específico");
            System.out.println("3. Criar novo relacionamento");
            System.out.println("4. Atualizar valor investido");
            System.out.println("5. Deletar relacionamento");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("Digite o ID da empresa: ");
                        int companyId = readPositiveInt();
                        List<Integer> userIds = userCompanyRelationDAO.findUsersByCompanyId(companyId);
                        System.out.println("\n👥 Total: " + userIds.size() + " usuário(s)");
                        System.out.println("IDs: " + userIds);
                        break;

                    case 2:
                        System.out.println("\n=== BUSCAR RELACIONAMENTO ===");
                        System.out.print("ID do usuário: ");
                        int findUserId = readPositiveInt();
                        System.out.print("ID da empresa: ");
                        int findCompanyId = readPositiveInt();

                        Map<String, Object> rel = userCompanyRelationDAO.findByKeys(findUserId, findCompanyId);

                        if (rel != null) {
                            System.out.println("✅ Relacionamento encontrado:");
                            System.out.printf("- Usuário ID: %d | Empresa ID: %d | Investido: $%.2f | Data: %s%n",
                                    rel.get("userId"), rel.get("companyId"), rel.get("amount"), rel.get("date"));
                        } else {
                            System.out.println("❌ Relacionamento não encontrado.");
                        }
                        break;

                    case 3:
                        System.out.println("\n=== CRIAR RELACIONAMENTO ===");
                        System.out.print("ID do usuário: ");
                        int userId = readPositiveInt();

                        System.out.print("ID da empresa: ");
                        int newCompanyId = readPositiveInt();

                        System.out.print("Valor investido: $");
                        double amount = readPositiveDouble();

                        userCompanyRelationDAO.insert(userId, newCompanyId, amount, LocalDate.now());
                        break;

                    case 4:
                        System.out.println("\n=== ATUALIZAR VALOR INVESTIDO ===");
                        System.out.print("ID do usuário: ");
                        int updateUserId = readPositiveInt();

                        System.out.print("ID da empresa: ");
                        int updateCompanyId = readPositiveInt();

                        System.out.print("Novo valor investido: $");
                        double newAmount = readPositiveDouble();

                        userCompanyRelationDAO.updateInvestedAmount(updateUserId, updateCompanyId, newAmount);
                        break;

                    case 5:
                        System.out.println("\n=== DELETAR RELACIONAMENTO ===");
                        System.out.print("ID do usuário: ");
                        int delUserId = readPositiveInt();

                        System.out.print("ID da empresa: ");
                        int delCompanyId = readPositiveInt();

                        System.out.print("Digite 'SIM' para confirmar: ");
                        String confirmacao = scanner.nextLine();

                        if ("SIM".equalsIgnoreCase(confirmacao.trim())) {
                            userCompanyRelationDAO.delete(delUserId, delCompanyId);
                        } else {
                            System.out.println("❌ Exclusão cancelada.");
                        }
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("❌ Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    // ============================================================================
    // CRUD - COMPANY CRYPTO ASSET
    // ============================================================================

    private static void crudCompanyCryptoAsset() {
        while (true) {
            System.out.println("\n" + "─".repeat(80));
            System.out.println("CRUD - ALOCAÇÃO DE ATIVOS PARA EMPRESAS");
            System.out.println("─".repeat(80));
            System.out.println("1. Listar ativos de uma empresa");
            System.out.println("2. Buscar alocação específica");
            System.out.println("3. Adicionar ativo para empresa");
            System.out.println("4. Atualizar quantidade de ativo");
            System.out.println("5. Remover ativo de empresa");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("Digite o ID da empresa: ");
                        int companyId = readPositiveInt();
                        List<CryptoAsset> assets = companyCryptoAssetDAO.getAssetsByCompanyId(companyId);
                        System.out.println("\n💎 Total: " + assets.size() + " ativo(s)");
                        for (CryptoAsset asset : assets) {
                            System.out.printf("- %s (%s): %.4f @ $%.2f%n",
                                    asset.getName(), asset.getSymbol(), asset.getQuantity(), asset.getPrice());
                        }
                        break;

                    case 2:
                        System.out.println("\n=== BUSCAR ALOCAÇÃO ===");
                        System.out.print("ID da empresa: ");
                        int findCompanyId = readPositiveInt();
                        System.out.print("ID do ativo cripto: ");
                        int findAssetId = readPositiveInt();

                        CryptoAsset asset = companyCryptoAssetDAO.findByKeys(findCompanyId, findAssetId);

                        if (asset != null) {
                            System.out.println("✅ Alocação encontrada:");
                            System.out.printf("- %s (%s): %.4f (Quantidade alocada)%n",
                                    asset.getName(), asset.getSymbol(), asset.getQuantity());
                        } else {
                            System.out.println("❌ Alocação não encontrada.");
                        }
                        break;

                    case 3:
                        System.out.println("\n=== ADICIONAR ATIVO ===");
                        System.out.print("ID da empresa: ");
                        int newCompanyId = readPositiveInt();

                        System.out.print("ID do ativo cripto: ");
                        int assetId = readPositiveInt();

                        System.out.print("Quantidade a adicionar: ");
                        double quantity = readPositiveDouble();

                        companyCryptoAssetDAO.addOrUpdateAssetForCompany(newCompanyId, assetId, quantity);
                        break;

                    case 4:
                        System.out.println("\n=== ATUALIZAR QUANTIDADE ===");
                        System.out.print("ID da empresa: ");
                        int updateCompanyId = readPositiveInt();

                        System.out.print("ID do ativo cripto: ");
                        int updateAssetId = readPositiveInt();

                        System.out.print("Nova Quantidade (define o valor total): ");
                        double newQuantity = readPositiveDouble();

                        companyCryptoAssetDAO.updateAssetQuantity(updateCompanyId, updateAssetId, newQuantity);
                        break;

                    case 5:
                        System.out.println("\n=== REMOVER ATIVO ===");
                        System.out.print("ID da empresa: ");
                        int delCompanyId = readPositiveInt();

                        System.out.print("ID do ativo cripto: ");
                        int delAssetId = readPositiveInt();

                        System.out.print("Digite 'SIM' para confirmar: ");
                        String confirmacao = scanner.nextLine();

                        if ("SIM".equalsIgnoreCase(confirmacao.trim())) {
                            companyCryptoAssetDAO.removeAssetFromCompany(delCompanyId, delAssetId);
                        } else {
                            System.out.println("❌ Exclusão cancelada.");
                        }
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("❌ Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    // ============================================================================
    // CRUD - WALLET CRYPTO ASSET
    // ============================================================================

    private static void crudWalletCryptoAsset() {
        while (true) {
            System.out.println("\n" + "─".repeat(80));
            System.out.println("CRUD - ATIVOS EM CARTEIRAS");
            System.out.println("─".repeat(80));
            System.out.println("1. Listar ativos de uma carteira");
            System.out.println("2. Buscar ativo específico na carteira");
            System.out.println("3. Adicionar ativo à carteira");
            System.out.println("4. Atualizar quantidade de ativo");
            System.out.println("5. Remover ativo da carteira");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("Digite o ID da carteira: ");
                        int walletId = readPositiveInt();
                        List<CryptoAsset> assets = walletCryptoAssetDAO.findCryptoAssetsByWallet(walletId);
                        System.out.println("\n💼 Total: " + assets.size() + " ativo(s)");
                        for (CryptoAsset asset : assets) {
                            System.out.printf("- %s (%s): %.4f @ $%.2f = $%.2f%n",
                                    asset.getName(), asset.getSymbol(), asset.getQuantity(),
                                    asset.getPrice(), asset.getTotalValue());
                        }
                        break;

                    case 2:
                        System.out.println("\n=== BUSCAR ATIVO NA CARTEIRA ===");
                        System.out.print("ID da carteira: ");
                        int findWalletId = readPositiveInt();
                        System.out.print("ID do ativo cripto: ");
                        int findAssetId = readPositiveInt();

                        CryptoAsset asset = walletCryptoAssetDAO.findByKeys(findWalletId, findAssetId);

                        if (asset != null) {
                            System.out.println("✅ Ativo encontrado na carteira:");
                            System.out.printf("- %s (%s): %.4f (Quantidade na carteira)%n",
                                    asset.getName(), asset.getSymbol(), asset.getQuantity());
                        } else {
                            System.out.println("❌ Ativo não encontrado nesta carteira.");
                        }
                        break;

                    case 3:
                        System.out.println("\n=== ADICIONAR ATIVO ===");
                        System.out.print("ID da carteira: ");
                        int newWalletId = readPositiveInt();

                        System.out.print("ID do ativo cripto: ");
                        int assetId = readPositiveInt();

                        System.out.print("Quantidade: ");
                        double quantity = readPositiveDouble();

                        walletCryptoAssetDAO.addCryptoAssetToWallet(newWalletId, assetId, quantity);
                        break;

                    case 4:
                        System.out.println("\n=== ATUALIZAR QUANTIDADE ===");
                        System.out.print("ID da carteira: ");
                        int updateWalletId = readPositiveInt();

                        System.out.print("ID do ativo cripto: ");
                        int updateAssetId = readPositiveInt();

                        System.out.print("Nova quantidade: ");
                        double newQuantity = readPositiveDouble();

                        walletCryptoAssetDAO.updateCryptoAssetQuantity(updateWalletId, updateAssetId, newQuantity);
                        break;

                    case 5:
                        System.out.println("\n=== REMOVER ATIVO ===");
                        System.out.print("ID da carteira: ");
                        int delWalletId = readPositiveInt();

                        System.out.print("ID do ativo cripto: ");
                        int delAssetId = readPositiveInt();

                        System.out.print("Digite 'SIM' para confirmar: ");
                        String confirmacao = scanner.nextLine();

                        if ("SIM".equalsIgnoreCase(confirmacao.trim())) {
                            walletCryptoAssetDAO.removeCryptoAssetFromWallet(delWalletId, delAssetId);
                        } else {
                            System.out.println("❌ Exclusão cancelada.");
                        }
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("❌ Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    // ============================================================================
    // MÉTODOS AUXILIARES E DE VALIDAÇÃO
    // ============================================================================
    
    private static int getNextUserIdFromCache() {
        if (userCache.isEmpty()) {
            return User.getNextAvailableId();
        }

        int maxId = userCache.stream()
                .mapToInt(User::getId)
                .max()
                .orElse(0);
        return maxId + 1;
    }

    private static int readInt() {
        try {
            int valor = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer
            return valor;
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Limpar buffer
            throw new IllegalArgumentException("Valor inválido! Digite um número inteiro.");
        }
    }

    // Novo método: Lê inteiro positivo e valida
    private static int readPositiveInt() {
        while (true) {
            try {
                int valor = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer
                if (valor <= 0) {
                    System.out.print("❌ O valor deve ser positivo! Tente novamente: ");
                    continue;
                }
                return valor;
            } catch (InputMismatchException e) {
                scanner.nextLine(); // Limpar buffer
                System.out.print("❌ Valor inválido! Digite um número inteiro: ");
            }
        }
    }

    private static double readDouble() {
        try {
            double valor = scanner.nextDouble();
            scanner.nextLine(); // Limpar buffer
            return valor;
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Limpar buffer
            throw new IllegalArgumentException("Valor inválido! Digite um número.");
        }
    }

    // Novo método: Lê double positivo e valida
    private static double readPositiveDouble() {
        while (true) {
            try {
                double valor = scanner.nextDouble();
                scanner.nextLine(); // Limpar buffer
                if (valor < 0) {
                    System.out.print("❌ O valor não pode ser negativo! Tente novamente: ");
                    continue;
                }
                return valor;
            } catch (InputMismatchException e) {
                scanner.nextLine(); // Limpar buffer
                System.out.print("❌ Valor inválido! Digite um número: ");
            }
        }
    }

    // Novo método: Lê string e garante que não é vazia
    private static String readStringNotEmpty(String fieldName) {
        while (true) {
            System.out.print(fieldName + ": ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("❌ O campo '" + fieldName + "' não pode ser vazio!");
        }
    }

    // Novo método: Valida formato de email
    private static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private static void loadDataToMemory() {
        try {
            System.out.println("⏳ Carregando dados do banco de dados para a memória...");
            userCache = User.getAllUsers();
            companyCache = companyDAO.findAll();
            cryptoAssetCache = cryptoAssetDAO.findAll();

            System.out.printf("✅ Dados carregados: %d Usuários, %d Empresas, %d Ativos Cripto.%n", userCache.size(), companyCache.size(), cryptoAssetCache.size());
        } catch (Exception e) {
            System.err.println("❌ Erro crítico ao carregar dados para a memória: " + e.getMessage());
            e.printStackTrace();
            System.out.println("A aplicação não pode continuar sem os dados iniciais. Encerrando.");
            System.exit(1);
        }
    }

    private static void initializeDAOs() {
        System.out.println("🔧 Inicializando DAOs...");
        companyDAO = new CompanyDAO();
        cryptoAssetDAO = new CryptoAssetDAO();
        walletDAO = new WalletDAO();
        marketDAO = new MarketDAO();
        transactionDAO = new TransactionDAO();
        companyCryptoAssetDAO = new CompanyCryptoAssetDAO();
        walletCryptoAssetDAO = new WalletCryptoAssetDAO();
        userCompanyRelationDAO = new UserCompanyRelationDAO();
        System.out.println("✅ DAOs inicializados com sucesso!\n");
    }

    private static void printHeader() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("    ⚡ VOLTZ CRYPTO MANAGEMENT SYSTEM v3.0");
        System.out.println("    🔬 Sistema Interativo com CRUD e Testes Automatizados");
        System.out.println("═".repeat(80));
        System.out.println("📅 Data: " + LocalDate.now());
        System.out.println("🏛️  Instituição: FIAP");
        System.out.println("═".repeat(80) + "\n");
    }

    // ============================================================================
    // TESTES AUTOMATIZADOS
    // ============================================================================

    private static void runAutomatedTests(boolean verbose) {
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;

        if (!verbose) {
            System.out.println("\n⏳ Executando testes em modo silencioso...");
            System.out.println("(Aguarde... isso pode levar alguns minutos)\n");
        }

        try {
            testOracleConnection(verbose);

            if (verbose) {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("FASE 1: TESTES DE DAOs BÁSICOS");
                System.out.println("=".repeat(80));
            }
            testUserCRUD(verbose);
            testCompanyDAO(verbose);
            testCryptoAssetDAO(verbose);
            testWalletDAO(verbose);

            if (verbose) {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("FASE 2: TESTES DE MERCADO");
                System.out.println("=".repeat(80));
            }
            testMarketDAO(verbose);
            testTransactionDAO(verbose);

            if (verbose) {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("FASE 3: TESTES DE RELACIONAMENTO");
                System.out.println("=".repeat(80));
            }
            testUserCompanyRelationDAO(verbose);
            testCompanyCryptoAssetDAO(verbose);
            testWalletCryptoAssetDAO(verbose);

            printStatistics();

        } catch (Exception e) {
            System.err.println("❌ ERRO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (verbose) {
                System.out.println("\n✅ Testes concluídos!");
            }
        }
    }

    private static void testOracleConnection(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: Conexão Oracle ━━━");
        totalTests++;
        try {
            java.sql.Connection conn = OracleConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                if (verbose) System.out.println("✅ Conexão estabelecida");
                passedTests++;
            } else {
                if (verbose) System.out.println("❌ Falha na conexão");
                failedTests++;
            }
        } catch (Exception e) {
            if (verbose) System.err.println("❌ Erro: " + e.getMessage());
            failedTests++;
        }
    }

    private static void testUserCRUD(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: User CRUD ━━━");
        totalTests++;
        try {
            int nextId = User.getNextAvailableId();
            User testUser = new User("Test User", nextId, "test" + nextId + "@test.com", "pass");
            testUser.insert();

            User found = User.findById(nextId);
            if (found != null) {
                if (verbose) System.out.println("✅ User CRUD funcionando");
                passedTests++;
                testUser.delete();
            } else {
                if (verbose) System.out.println("❌ User não encontrado");
                failedTests++;
            }
        } catch (Exception e) {
            if (verbose) System.err.println("❌ Erro: " + e.getMessage());
            failedTests++;
        }
    }

    private static void testCompanyDAO(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: Company CRUD ━━━");
        totalTests++;
        try {
            Company test = new Company("TestCorp", 99999, "TEST-99999");
            boolean inserted = companyDAO.insert(test);

            if (inserted) {
                Company found = companyDAO.findById(99999);
                if (found != null) {
                    if (verbose) System.out.println("✅ Company CRUD funcionando");
                    passedTests++;
                    companyDAO.delete(99999);
                } else {
                    if (verbose) System.out.println("❌ Company não encontrada");
                    failedTests++;
                }
            } else {
                if (verbose) System.out.println("❌ Falha ao inserir");
                failedTests++;
            }
        } catch (Exception e) {
            if (verbose) System.err.println("❌ Erro: " + e.getMessage());
            failedTests++;
        }
    }

    private static void testCryptoAssetDAO(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: CryptoAsset CRUD ━━━");
        totalTests++;
        try {
            String symbol = "TST" + (int)(Math.random() * 100);
            CryptoAsset test = new CryptoAsset("TestCoin", symbol, 100.0, 1.0);
            boolean inserted = cryptoAssetDAO.insert(test);

            if (inserted) {
                if (verbose) System.out.println("✅ CryptoAsset CRUD funcionando");
                passedTests++;
                cryptoAssetDAO.delete(symbol);
            } else {
                if (verbose) System.out.println("❌ Falha ao inserir");
                failedTests++;
            }
        } catch (Exception e) {
            if (verbose) System.err.println("❌ Erro: " + e.getMessage());
            failedTests++;
        }
    }

    private static void testWalletDAO(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: Wallet CRUD ━━━");
        totalTests++;
        try {
            Wallet test = new Wallet(99998, 1, "Test Wallet");
            walletDAO.insert(test);

            Wallet found = walletDAO.findById(99998);
            if (found != null) {
                if (verbose) System.out.println("✅ Wallet CRUD funcionando");
                passedTests++;
                walletDAO.delete(99998);
            } else {
                if (verbose) System.out.println("❌ Wallet não encontrada");
                failedTests++;
            }
        } catch (Exception e) {
            if (verbose) System.err.println("❌ Erro: " + e.getMessage());
            failedTests++;
        }
    }

    private static void testMarketDAO(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: Market DAO ━━━");
        totalTests++;
        try {
            marketDAO.save("TEST", 100.0);
            Double price = marketDAO.getPrice("TEST");

            if (price != null && price == 100.0) {
                if (verbose) System.out.println("✅ Market DAO funcionando");
                passedTests++;
                marketDAO.delete("TEST");
            } else {
                if (verbose) System.out.println("❌ Preço não encontrado");
                failedTests++;
            }
        } catch (Exception e) {
            if (verbose) System.err.println("❌ Erro: " + e.getMessage());
            failedTests++;
        }
    }

    private static void testTransactionDAO(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: Transaction DAO ━━━");
        totalTests++;
        try {
            CryptoAsset asset = cryptoAssetDAO.findById(1);
            if (asset != null) {
                Transaction test = new Transaction(asset, 0.1, "BUY");
                transactionDAO.insert(test, 1, 1);

                List<Transaction> found = transactionDAO.findByUserId(1);
                if (!found.isEmpty()) {
                    if (verbose) System.out.println("✅ Transaction DAO funcionando");
                    passedTests++;
                } else {
                    if (verbose) System.out.println("❌ Transação não encontrada");
                    failedTests++;
                }
            } else {
                if (verbose) System.out.println("⚠️ Teste pulado (sem ativo id=1)");
                totalTests--;
            }
        } catch (Exception e) {
            if (verbose) System.err.println("❌ Erro: " + e.getMessage());
            failedTests++;
        }
    }

    private static void testUserCompanyRelationDAO(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: UserCompanyRelation DAO ━━━");
        totalTests++;
        try {
            userCompanyRelationDAO.insert(1, 1, 1000.0, LocalDate.now());
            List<Integer> users = userCompanyRelationDAO.findUsersByCompanyId(1);

            if (!users.isEmpty()) {
                if (verbose) System.out.println("✅ UserCompanyRelation DAO funcionando");
                passedTests++;
                userCompanyRelationDAO.delete(1, 1);
            } else {
                if (verbose) System.out.println("❌ Relação não encontrada");
                failedTests++;
            }
        } catch (Exception e) {
            if (verbose) System.err.println("❌ Erro: " + e.getMessage());
            failedTests++;
        }
    }

    private static void testCompanyCryptoAssetDAO(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: CompanyCryptoAsset DAO ━━━");
        totalTests++;
        try {
            companyCryptoAssetDAO.addOrUpdateAssetForCompany(1, 1, 1.0);
            List<CryptoAsset> assets = companyCryptoAssetDAO.getAssetsByCompanyId(1);

            if (!assets.isEmpty()) {
                if (verbose) System.out.println("✅ CompanyCryptoAsset DAO funcionando");
                passedTests++;
                companyCryptoAssetDAO.removeAssetFromCompany(1, 1);
            } else {
                if (verbose) System.out.println("❌ Ativo não encontrado");
                failedTests++;
            }
        } catch (Exception e) {
            if (verbose) System.err.println("❌ Erro: " + e.getMessage());
            failedTests++;
        }
    }

    private static void testWalletCryptoAssetDAO(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: WalletCryptoAsset DAO ━━━");
        totalTests++;
        try {
            Wallet tempWallet = new Wallet(99997, 1, "Temp Test");
            walletDAO.insert(tempWallet);

            walletCryptoAssetDAO.addCryptoAssetToWallet(99997, 1, 0.5);
            List<CryptoAsset> assets = walletCryptoAssetDAO.findCryptoAssetsByWallet(99997);

            if (!assets.isEmpty()) {
                if (verbose) System.out.println("✅ WalletCryptoAsset DAO funcionando");
                passedTests++;
            } else {
                if (verbose) System.out.println("❌ Ativo não encontrado");
                failedTests++;
            }

            walletCryptoAssetDAO.removeCryptoAssetFromWallet(99997, 1);
            walletDAO.delete(99997);

        } catch (Exception e) {
            if (verbose) System.err.println("❌ Erro: " + e.getMessage());
            failedTests++;
        }
    }

    private static void printStatistics() {
        System.out.println("\n" + "═".repeat(80));
        System.out.println("📊 ESTATÍSTICAS FINAIS DOS TESTES");
        System.out.println("═".repeat(80));
        System.out.println("✅ Testes Aprovados:  " + passedTests);
        System.out.println("❌ Testes Reprovados: " + failedTests);
        System.out.println("📈 Total de Testes:   " + totalTests);

        if (totalTests > 0) {
            double successRate = (passedTests * 100.0) / totalTests;
            System.out.printf("🎯 Taxa de Sucesso:   %.1f%%%n", successRate);

            if (successRate == 100.0) {
                System.out.println("\n🎉 PARABÉNS! Todos os testes passaram com sucesso!");
            } else if (successRate >= 80.0) {
                System.out.println("\n👍 Bom trabalho! A maioria dos testes passou.");
            } else {
                System.out.println("\n⚠️  Atenção! Vários testes falharam.");
            }
        }
        System.out.println("═".repeat(80));
    }
}
