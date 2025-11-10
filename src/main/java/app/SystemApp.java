package app;

import dao.*;
import model.*;
import report.Report;
import db.OracleConnection;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

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
                        int id = readInt();
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
                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();

                        System.out.print("Email: ");
                        String email = scanner.nextLine();

                        System.out.print("Senha: ");
                        String senha = scanner.nextLine();

                        int novoId = getNextUserIdFromCache();
                        User novoUser = new User(nome, novoId, email, senha);
                        novoUser.insert();
                        userCache.add(novoUser);
                        System.out.println("✅ Usuário criado e adicionado ao cache.");
                        break;

                    case 4:
                        System.out.print("Digite o ID do usuário a atualizar: ");
                        int updateId = readInt();
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
                                userToUpdate.setName(newName);
                            }

                            System.out.print("Novo email (Enter para manter atual): ");
                            String newEmail = scanner.nextLine();
                            if (!newEmail.trim().isEmpty()) {
                                userToUpdate.setEmail(newEmail);
                            }

                            System.out.print("Nova senha (Enter para manter atual): ");
                            String newPassword = scanner.nextLine();
                            if (!newPassword.trim().isEmpty()) {
                                userToUpdate.setPassword(newPassword);
                            }

                            userToUpdate.update();
                            System.out.println("✅ Usuário atualizado no BD e cache.");
                        } else {
                            System.out.println("❌ Usuário não encontrado!");
                        }
                        break;

                    case 5:
                        System.out.print("Digite o ID do usuário a deletar: ");
                        int deleteId = readInt();
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
                        int id = readInt();
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
                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();

                        System.out.print("ID (número): ");
                        int novoId = readInt();

                        System.out.print("Identificador (CNPJ/código): ");
                        String identificador = scanner.nextLine();

                        Company novaCompany = new Company(nome, novoId, identificador);
                        companyDAO.insert(novaCompany);
                        companyCache.add(novaCompany);
                        System.out.println("✅ Empresa criada e adicionada ao cache.");
                        break;

                    case 4:
                        System.out.print("Digite o ID da empresa a atualizar: ");
                        int updateId = readInt();
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
                                companyToUpdate.setName(newName);
                            }

                            System.out.print("Novo identificador (Enter para manter): ");
                            String newId = scanner.nextLine();
                            if (!newId.trim().isEmpty()) {
                                companyToUpdate.setIdentifier(newId);
                            }

                            companyDAO.update(companyToUpdate);
                            System.out.println("✅ Empresa atualizada no DB e cache.");
                        } else {
                            System.out.println("❌ Empresa não encontrada!");
                        }
                        break;

                    case 5:
                        System.out.print("Digite o ID da empresa a deletar: ");
                        int deleteId = readInt();
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
                        int id = readInt();
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
                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();

                        System.out.print("Símbolo (máx 10 caracteres, ex: BTC, ETH): ");
                        String simbolo = scanner.nextLine();
                        if (simbolo.length() > 10) {
                            System.out.println("❌ Símbolo muito longo! Máximo 10 caracteres.");
                            break;
                        }

                        System.out.print("Quantidade inicial: ");
                        double quantidade = readDouble();

                        System.out.print("Preço: ");
                        double preco = readDouble();

                        CryptoAsset novoAsset = new CryptoAsset(nome, simbolo, quantidade, preco);
                        cryptoAssetDAO.insert(novoAsset);
                        cryptoAssetCache.add(novoAsset);
                        System.out.println("✅ Ativo criado e adicionado ao cache.");
                        break;

                    case 4:
                        System.out.print("Digite o símbolo do ativo a atualizar (ex: BTC): ");
                        String symbolToUpdate = scanner.nextLine();

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
                            if (!newName.trim().isEmpty()) assetToUpdate.setName(newName);

                            System.out.print("Nova quantidade (Enter para manter): ");
                            String qtyStr = scanner.nextLine();
                            double newQty = qtyStr.trim().isEmpty() ? assetToUpdate.getQuantity() : Double.parseDouble(qtyStr);
                            if (!qtyStr.trim().isEmpty()) assetToUpdate.setQuantity(newQty);

                            System.out.print("Novo preço (Enter para manter): ");
                            String priceStr = scanner.nextLine();
                            double newPrice = priceStr.trim().isEmpty() ? assetToUpdate.getPrice() : Double.parseDouble(priceStr);
                            if (!priceStr.trim().isEmpty()) assetToUpdate.setPrice(newPrice);

                            cryptoAssetDAO.update(assetToUpdate, symbolToUpdate);
                            System.out.println("✅ Ativo atualizado no DB e cache.");
                        } else {
                            System.out.println("❌ Ativo não encontrado!");
                        }
                        break;

                    case 5:
                        System.out.print("Digite o símbolo do ativo a deletar: ");
                        String symbolToDelete = scanner.nextLine();

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
                        int id = readInt();
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
                        int novoId = readInt();

                        System.out.print("ID do usuário proprietário: ");
                        int userId = readInt();

                        System.out.print("Nome da carteira: ");
                        String nome = scanner.nextLine();

                        Wallet novaWallet = new Wallet(novoId, userId, nome);
                        walletDAO.insert(novaWallet);
                        break;

                    case 4:
                        System.out.print("Digite o ID da carteira a atualizar: ");
                        int updateId = readInt();
                        Wallet walletToUpdate = walletDAO.findById(updateId);

                        if (walletToUpdate != null) {
                            System.out.printf("Dados atuais: ID: %d | User ID: %d%n",
                                    walletToUpdate.getId(), walletToUpdate.getUserId());

                            System.out.print("Novo ID do usuário (Enter para manter): ");
                            String userIdStr = scanner.nextLine();
                            int newUserId = userIdStr.trim().isEmpty() ?
                                    walletToUpdate.getUserId() : Integer.parseInt(userIdStr);

                            Wallet updated = new Wallet(updateId, newUserId, "Wallet " + updateId);
                            walletDAO.update(updated);
                        } else {
                            System.out.println("❌ Carteira não encontrada!");
                        }
                        break;

                    case 5:
                        System.out.print("Digite o ID da carteira a deletar: ");
                        int deleteId = readInt();

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
            System.out.println("1. Listar todos os preços (Listar)");
            System.out.println("2. Buscar preço por símbolo (Buscar)");
            System.out.println("3. Criar novo preço (Inclusão)");
            System.out.println("4. Atualizar preço (Alteração)");
            System.out.println("5. Deletar preço (Exclusão)");
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
                        String symbol = scanner.nextLine().toUpperCase();
                        Double price = marketDAO.getPrice(symbol);
                        if (price != null) {
                            System.out.printf("💰 %s: $%,.2f%n", symbol, price);
                        } else {
                            System.out.println("❌ Preço não encontrado!");
                        }
                        break;

                    case 3: // <-- LÓGICA DE INCLUSÃO
                        System.out.println("\n=== CRIAR NOVO PREÇO ===");
                        System.out.print("Símbolo: ");
                        String newSymbol = scanner.nextLine().toUpperCase();

                        // Verifica se já existe
                        if (marketDAO.getPrice(newSymbol) != null) {
                            System.out.println("❌ Erro: Símbolo já existe. Use a Opção 4 para atualizar.");
                            break;
                        }

                        System.out.print("Preço: $");
                        double newPrice = readDouble();

                        marketDAO.save(newSymbol, newPrice);
                        break;

                    case 4: // <-- LÓGICA DE ALTERAÇÃO
                        System.out.println("\n=== ATUALIZAR PREÇO ===");
                        System.out.print("Símbolo: ");
                        String updateSymbol = scanner.nextLine().toUpperCase();

                        // Verifica se existe antes de atualizar
                        Double currentPrice = marketDAO.getPrice(updateSymbol);
                        if (currentPrice == null) {
                            System.out.println("❌ Erro: Símbolo não encontrado. Use a Opção 3 para criar.");
                            break;
                        }

                        System.out.printf("Preço atual de %s: $%,.2f%n", updateSymbol, currentPrice);
                        System.out.print("Novo preço: $");
                        double updatePrice = readDouble();

                        marketDAO.save(updateSymbol, updatePrice);
                        break;

                    case 5: // <-- MUDOU DE 4 PARA 5
                        System.out.print("Digite o símbolo a deletar: ");
                        String deleteSymbol = scanner.nextLine().toUpperCase();

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
                        int userId = readInt();
                        List<Transaction> transactions = transactionDAO.findByUserId(userId);
                        System.out.println("\n📝 Total: " + transactions.size() + " transação(ões)");
                        transactions.forEach(Transaction::showTransaction);
                        break;

                    case 2:
                        System.out.print("Digite o ID da transação: ");
                        int findId = readInt();

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
                        int newUserId = readInt();

                        System.out.print("ID do ativo cripto: ");
                        int assetId = readInt();

                        CryptoAsset asset = cryptoAssetDAO.findById(assetId);
                        if (asset == null) {
                            System.out.println("❌ Ativo não encontrado!");
                            break;
                        }

                        System.out.print("Quantidade: ");
                        double amount = readDouble();

                        System.out.print("Tipo (BUY/SELL): ");
                        String type = scanner.nextLine().toUpperCase();

                        if (!type.equals("BUY") && !type.equals("SELL")) {
                            System.out.println("❌ Tipo inválido! Use BUY ou SELL.");
                            break;
                        }

                        Transaction newTransaction = new Transaction(asset, amount, type);
                        transactionDAO.insert(newTransaction, newUserId, assetId);
                        break;

                    case 4:
                        System.out.print("Digite o ID da transação a atualizar: ");
                        int updateId = readInt();

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

                        System.out.print("Novo tipo (BUY/SELL) (Enter para manter): ");
                        String newType = scanner.nextLine().toUpperCase();
                        if (newType.trim().isEmpty()) {
                            newType = txToUpdate.getType();
                        }

                        if (!newType.equals("BUY") && !newType.equals("SELL")) {
                            System.out.println("❌ Tipo inválido! Use BUY ou SELL.");
                            break;
                        }

                        txToUpdate.setAmount(newAmount);
                        txToUpdate.setType(newType);
                        transactionDAO.update(txToUpdate);

                        System.out.println("✅ Transação atualizada!");
                        break;
                    // ================================

                    case 5:
                        System.out.print("Digite o ID da transação a deletar: ");
                        int deleteId = readInt();

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
                        int companyId = readInt();
                        List<Integer> userIds = userCompanyRelationDAO.findUsersByCompanyId(companyId);
                        System.out.println("\n👥 Total: " + userIds.size() + " usuário(s)");
                        System.out.println("IDs: " + userIds);
                        break;

                    case 2:
                        System.out.println("\n=== BUSCAR RELACIONAMENTO ===");
                        System.out.print("ID do usuário: ");
                        int findUserId = readInt();
                        System.out.print("ID da empresa: ");
                        int findCompanyId = readInt();

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
                        int userId = readInt();

                        System.out.print("ID da empresa: ");
                        int newCompanyId = readInt();

                        System.out.print("Valor investido: $");
                        double amount = readDouble();

                        userCompanyRelationDAO.insert(userId, newCompanyId, amount, LocalDate.now());
                        break;

                    case 4:
                        System.out.println("\n=== ATUALIZAR VALOR INVESTIDO ===");
                        System.out.print("ID do usuário: ");
                        int updateUserId = readInt();

                        System.out.print("ID da empresa: ");
                        int updateCompanyId = readInt();

                        System.out.print("Novo valor investido: $");
                        double newAmount = readDouble();

                        userCompanyRelationDAO.updateInvestedAmount(updateUserId, updateCompanyId, newAmount);
                        break;

                    case 5:
                        System.out.println("\n=== DELETAR RELACIONAMENTO ===");
                        System.out.print("ID do usuário: ");
                        int delUserId = readInt();

                        System.out.print("ID da empresa: ");
                        int delCompanyId = readInt();

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
            System.out.println("3. Adicionar ativo para empresa (Inclusão)");
            System.out.println("4. Atualizar quantidade de ativo (Alteração)");
            System.out.println("5. Remover ativo de empresa (Exclusão)");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("Digite o ID da empresa: ");
                        int companyId = readInt();
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
                        int findCompanyId = readInt();
                        System.out.print("ID do ativo cripto: ");
                        int findAssetId = readInt();

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
                        System.out.println("\n=== ADICIONAR ATIVO (INCLUSÃO) ===");
                        System.out.print("ID da empresa: ");
                        int newCompanyId = readInt();

                        System.out.print("ID do ativo cripto: ");
                        int assetId = readInt();

                        System.out.print("Quantidade a adicionar: ");
                        double quantity = readDouble();

                        companyCryptoAssetDAO.addOrUpdateAssetForCompany(newCompanyId, assetId, quantity);
                        break;

                    case 4:
                        System.out.println("\n=== ATUALIZAR QUANTIDADE (ALTERAÇÃO) ===");
                        System.out.print("ID da empresa: ");
                        int updateCompanyId = readInt();

                        System.out.print("ID do ativo cripto: ");
                        int updateAssetId = readInt();

                        System.out.print("Nova Quantidade (define o valor total): ");
                        double newQuantity = readDouble();

                        companyCryptoAssetDAO.updateAssetQuantity(updateCompanyId, updateAssetId, newQuantity);
                        break;

                    case 5:
                        System.out.println("\n=== REMOVER ATIVO (EXCLUSÃO) ===");
                        System.out.print("ID da empresa: ");
                        int delCompanyId = readInt();

                        System.out.print("ID do ativo cripto: ");
                        int delAssetId = readInt();

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
            System.out.println("3. Adicionar ativo à carteira (Inclusão)");
            System.out.println("4. Atualizar quantidade de ativo (Alteração)");
            System.out.println("5. Remover ativo da carteira (Exclusão)");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");

            int opcao = readInt();

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("Digite o ID da carteira: ");
                        int walletId = readInt();
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
                        int findWalletId = readInt();
                        System.out.print("ID do ativo cripto: ");
                        int findAssetId = readInt();

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
                        System.out.println("\n=== ADICIONAR ATIVO (INCLUSÃO) ===");
                        System.out.print("ID da carteira: ");
                        int newWalletId = readInt();

                        System.out.print("ID do ativo cripto: ");
                        int assetId = readInt();

                        System.out.print("Quantidade: ");
                        double quantity = readDouble();

                        walletCryptoAssetDAO.addCryptoAssetToWallet(newWalletId, assetId, quantity);
                        break;

                    case 4:
                        System.out.println("\n=== ATUALIZAR QUANTIDADE (ALTERAÇÃO) ===");
                        System.out.print("ID da carteira: ");
                        int updateWalletId = readInt();

                        System.out.print("ID do ativo cripto: ");
                        int updateAssetId = readInt();

                        System.out.print("Nova quantidade: ");
                        double newQuantity = readDouble();

                        walletCryptoAssetDAO.updateCryptoAssetQuantity(updateWalletId, updateAssetId, newQuantity);
                        break;

                    case 5:
                        System.out.println("\n=== REMOVER ATIVO (EXCLUSÃO) ===");
                        System.out.print("ID da carteira: ");
                        int delWalletId = readInt();

                        System.out.print("ID do ativo cripto: ");
                        int delAssetId = readInt();

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
    // MÉTODOS DE CACHE
    // ============================================================================

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

    // ============================================================================
    // MÉTODOS AUXILIARES
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
    // TESTES AUTOMATIZADOS (continuação do código anterior...)
    // ============================================================================

    private static void runAutomatedTests(boolean verbose) {
        // Reset contadores
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

    // Implementações dos métodos de teste com parâmetro verbose
    // (Similar aos métodos anteriores, mas verificando if(verbose) antes de System.out.println)

    private static void testOracleConnection(boolean verbose) {
        if (verbose) System.out.println("\n━━━ TESTE: Conexão Oracle ━━━");
        totalTests++;
        try {
            Connection conn = OracleConnection.getConnection();
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
                if (verbose) System.out.println("⚠️ Teste pulado (sem ativo)");
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
            // Criar carteira temporária
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

            // Limpar
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

    private static void startTest(String name) {
        totalTests++;
    }

    private static void passTest() {
        passedTests++;
    }

    private static void failTest() {
        failedTests++;
    }

    private static void endTest() {
        // Método vazio mantido para compatibilidade
    }
}
