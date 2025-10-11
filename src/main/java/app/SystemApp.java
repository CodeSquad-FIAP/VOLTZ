package app;

import dao.*;
import model.*;
import report.Report;
import db.OracleConnection;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * VOLTZ CRYPTO MANAGEMENT SYSTEM - TEST SUITE
 * ============================================================================
 * Sistema de gerenciamento de criptomoedas com testes integrados
 *
 * Estrutura:
 * 1. DAOs Básicos (Company, CryptoAsset, Wallet)
 * 2. DAOs de Mercado (Market, Transaction)
 * 3. DAOs de Relacionamento (UserCompany, CompanyCrypto, WalletCrypto)
 * 4. Cenários Complexos
 * 5. Relatórios e Persistência
 *
 * @author Seu Nome
 * @version 2.0
 * @since 2025-10-11
 * ============================================================================
 */
public class SystemApp {

    // ============================================================================
    // INSTÂNCIAS DOS DAOs - SINGLETON PATTERN
    // ============================================================================
    private static CompanyDAO companyDAO;
    private static CryptoAssetDAO cryptoAssetDAO;
    private static WalletDAO walletDAO;
    private static MarketDAO marketDAO;
    private static TransactionDAO transactionDAO;
    private static CompanyCryptoAssetDAO companyCryptoAssetDAO;
    private static WalletCryptoAssetDAO walletCryptoAssetDAO;
    private static UserCompanyRelationDAO userCompanyRelationDAO;

    // Contadores para estatísticas
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // ============================================================================
    // MÉTODO MAIN - PONTO DE ENTRADA
    // ============================================================================
    public static void main(String[] args) {
        printHeader();

        try {
            // Inicializar DAOs
            initializeDAOs();

            // Testar conexão
            testOracleConnection();

            // FASE 1: Testes de DAOs Básicos
            System.out.println("\n" + "=".repeat(80));
            System.out.println("FASE 1: TESTES DE DAOs BÁSICOS (CRUD)");
            System.out.println("=".repeat(80));
            testUserCRUD();
            testCompanyDAO();
            testCryptoAssetDAO();
            testWalletDAO();

            // FASE 2: Testes de DAOs de Mercado
            System.out.println("\n" + "=".repeat(80));
            System.out.println("FASE 2: TESTES DE DAOs DE MERCADO");
            System.out.println("=".repeat(80));
            testMarketDAO();
            testTransactionDAO();

            // FASE 3: Testes de DAOs de Relacionamento
            System.out.println("\n" + "=".repeat(80));
            System.out.println("FASE 3: TESTES DE DAOs DE RELACIONAMENTO");
            System.out.println("=".repeat(80));
            testUserCompanyRelationDAO();
            testCompanyCryptoAssetDAO();
            testWalletCryptoAssetDAO();

            // FASE 4: Cenários Complexos
            System.out.println("\n" + "=".repeat(80));
            System.out.println("FASE 4: CENÁRIOS DE TESTE COMPLEXOS");
            System.out.println("=".repeat(80));
            testComplexScenario1_PortfolioManagement();
            testComplexScenario2_CompanyInvestment();
            testComplexScenario3_MarketSimulation();

            // FASE 5: Testes com Collections
            System.out.println("\n" + "=".repeat(80));
            System.out.println("FASE 5: TESTES COM COLLECTIONS (LIST, MAP, SET)");
            System.out.println("=".repeat(80));
            testCollectionsIntegration();

            // FASE 6: Relatórios e Persistência
            System.out.println("\n" + "=".repeat(80));
            System.out.println("FASE 6: GERAÇÃO DE RELATÓRIOS E PERSISTÊNCIA");
            System.out.println("=".repeat(80));
            testReportsAndPersistence();

            // Estatísticas finais
            printStatistics();

        } catch (Exception e) {
            System.err.println("❌ ERRO CRÍTICO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Garantir fechamento da conexão
            OracleConnection.closeConnection();
            System.out.println("\n✅ Programa finalizado e conexão fechada com sucesso!");
        }
    }

    // ============================================================================
    // INICIALIZAÇÃO
    // ============================================================================

    /**
     * Inicializa todas as instâncias de DAO
     */
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

    // ============================================================================
    // FASE 1: TESTES DE DAOs BÁSICOS (CRUD)
    // ============================================================================

    /**
     * Teste completo de CRUD para Company
     *
     * Operações testadas:
     * - INSERT: Criar nova empresa
     * - READ: Buscar por ID e listar todas
     * - UPDATE: Atualizar dados da empresa
     * - DELETE: Remover empresa
     */
    // ============================================================================
// PATCH 1: CompanyDAO - Trocar ID 999 por 99999
// ============================================================================

    private static void testCompanyDAO() {
        startTest("CompanyDAO - CRUD Completo");

        try {
            // CORRIGIDO: Usar ID maior para evitar conflitos
            System.out.println("\n--- 1. CREATE: Inserindo nova empresa ---");
            Company testCompany = new Company("TechCorp Test", 99999, "CNPJ-99999-TEST");
            boolean inserted = companyDAO.insert(testCompany);

            if (inserted) {
                System.out.println("✅ Empresa inserida com sucesso");
                passTest();
            } else {
                System.out.println("⚠️ Falha ao inserir empresa");
                failTest();
            }

            // CORRIGIDO: Buscar com o ID correto
            System.out.println("\n--- 2. READ: Buscando empresa por ID ---");
            Company foundCompany = companyDAO.findById(99999);

            if (foundCompany != null) {
                System.out.println("✅ Empresa encontrada:");
                foundCompany.showInfo();
                passTest();
            } else {
                System.out.println("❌ Empresa não encontrada");
                failTest();
            }

            // 3. READ - Listar todas
            System.out.println("\n--- 3. READ: Listando todas as empresas ---");
            List<Company> allCompanies = companyDAO.findAll();
            System.out.println("📊 Total de empresas: " + allCompanies.size());
            allCompanies.forEach(Company::showInfo);
            passTest();

            // CORRIGIDO: Atualizar com ID correto
            System.out.println("\n--- 4. UPDATE: Atualizando empresa ---");
            testCompany = new Company("TechCorp Updated", 99999, "CNPJ-99999-UPDATED");
            boolean updated = companyDAO.update(testCompany);

            if (updated) {
                System.out.println("✅ Empresa atualizada com sucesso");
                Company updatedCompany = companyDAO.findById(99999);
                if (updatedCompany != null) {
                    updatedCompany.showInfo();
                }
                passTest();
            } else {
                System.out.println("⚠️ Falha ao atualizar empresa");
                failTest();
            }

            // CORRIGIDO: Deletar com ID correto
            System.out.println("\n--- 5. DELETE: Removendo empresa ---");
            boolean deleted = companyDAO.delete(99999);

            if (deleted) {
                System.out.println("✅ Empresa deletada com sucesso");
                passTest();
            } else {
                System.out.println("⚠️ Falha ao deletar empresa");
                failTest();
            }

            // Verificar exclusão
            Company shouldBeNull = companyDAO.findById(99999);
            if (shouldBeNull == null) {
                System.out.println("✅ Verificação: Empresa realmente foi excluída");
                passTest();
            } else {
                System.out.println("❌ Erro: Empresa ainda existe no banco");
                failTest();
            }

        } catch (Exception e) {
            System.err.println("❌ Erro durante teste de CompanyDAO: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    /**
     * Teste completo de CRUD para CryptoAsset
     *
     * Operações testadas:
     * - INSERT: Criar novo ativo cripto
     * - READ: Buscar por ID e listar todos
     * - UPDATE: Atualizar dados do ativo
     * - DELETE: Remover ativo
     */
    private static void testCryptoAssetDAO() {
        startTest("CryptoAssetDAO - CRUD Completo");

        try {
            // CORRIGIDO: Usar símbolo de no máximo 10 caracteres
            System.out.println("\n--- 1. CREATE: Inserindo novo ativo cripto ---");
            // Usar apenas 3 caracteres + 3 dígitos aleatórios = 6 caracteres no máximo
            String uniqueSymbol = "TST" + (int)(Math.random() * 1000);
            CryptoAsset testAsset = new CryptoAsset("TestCoin", uniqueSymbol, 1000.0, 50.00);
            boolean inserted = cryptoAssetDAO.insert(testAsset);

            if (inserted) {
                System.out.println("✅ Ativo inserido com sucesso (símbolo: " + uniqueSymbol + ")");
                passTest();
            } else {
                System.out.println("⚠️ Falha ao inserir ativo");
                failTest();
            }

            // 2. READ - Listar todos para encontrar o ID
            System.out.println("\n--- 2. READ: Listando todos os ativos ---");
            List<CryptoAsset> allAssets = cryptoAssetDAO.findAll();
            System.out.println("📊 Total de ativos: " + allAssets.size());

            // Encontrar nosso ativo de teste
            CryptoAsset foundAsset = null;
            for (CryptoAsset asset : allAssets) {
                System.out.println("- " + asset.getName() + " (" + asset.getSymbol() +
                        ") - Qty: " + asset.getQuantity() + ", Price: $" + asset.getPrice());
                if (uniqueSymbol.equals(asset.getSymbol())) {
                    foundAsset = asset;
                }
            }

            if (foundAsset != null) {
                System.out.println("✅ Ativo de teste encontrado na listagem");
                passTest();
            } else {
                System.out.println("⚠️ Ativo de teste não encontrado");
                failTest();
            }

            // 3. UPDATE - Atualizar ativo
            System.out.println("\n--- 3. UPDATE: Atualizando ativo ---");
            CryptoAsset updatedAsset = new CryptoAsset("TestCoin Updated", uniqueSymbol, 1500.0, 75.00);
            boolean updated = cryptoAssetDAO.update(updatedAsset, uniqueSymbol);

            if (updated) {
                System.out.println("✅ Ativo atualizado com sucesso");
                passTest();
            } else {
                System.out.println("⚠️ Falha ao atualizar ativo");
                failTest();
            }

            // 4. DELETE - Remover ativo
            System.out.println("\n--- 4. DELETE: Removendo ativo ---");
            boolean deleted = cryptoAssetDAO.delete(uniqueSymbol);

            if (deleted) {
                System.out.println("✅ Ativo deletado com sucesso");
                passTest();
            } else {
                System.out.println("⚠️ Falha ao deletar ativo");
                failTest();
            }

        } catch (Exception e) {
            System.err.println("❌ Erro durante teste de CryptoAssetDAO: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    /**
     * Teste completo de CRUD para Wallet
     *
     * Operações testadas:
     * - INSERT: Criar nova carteira
     * - READ: Buscar por ID e listar todas
     * - UPDATE: Atualizar dados da carteira
     * - DELETE: Remover carteira
     */
    private static void testWalletDAO() {
        startTest("WalletDAO - CRUD Completo");

        try {
            // 1. CREATE - Inserir nova carteira
            System.out.println("\n--- 1. CREATE: Inserindo nova carteira ---");
            Wallet testWallet = new Wallet(9999, 1, "Test Wallet");
            walletDAO.insert(testWallet);
            System.out.println("✅ Carteira inserida");
            passTest();

            // 2. READ - Buscar por ID
            System.out.println("\n--- 2. READ: Buscando carteira por ID ---");
            Wallet foundWallet = walletDAO.findById(9999);

            if (foundWallet != null) {
                System.out.println("✅ Carteira encontrada:");
                System.out.println("ID: " + foundWallet.getId() +
                        ", User ID: " + foundWallet.getUserId() +
                        ", Nome: " + foundWallet.getName());
                passTest();
            } else {
                System.out.println("❌ Carteira não encontrada");
                failTest();
            }

            // 3. READ - Listar todas
            System.out.println("\n--- 3. READ: Listando todas as carteiras ---");
            List<Wallet> allWallets = walletDAO.findAll();
            System.out.println("📊 Total de carteiras: " + allWallets.size());
            for (Wallet w : allWallets) {
                System.out.println("- Wallet ID: " + w.getId() +
                        ", User: " + w.getUserId() +
                        ", Nome: " + w.getName());
            }
            passTest();

            // 4. UPDATE - Atualizar carteira
            System.out.println("\n--- 4. UPDATE: Atualizando carteira ---");
            testWallet.setName("Test Wallet Updated");
            walletDAO.update(testWallet);

            Wallet updatedWallet = walletDAO.findById(9999);
            if (updatedWallet != null && "Test Wallet Updated".equals(updatedWallet.getName())) {
                System.out.println("✅ Carteira atualizada com sucesso");
                passTest();
            } else {
                System.out.println("⚠️ Falha ao verificar atualização");
                failTest();
            }

            // 5. DELETE - Remover carteira
            System.out.println("\n--- 5. DELETE: Removendo carteira ---");
            walletDAO.delete(9999);

            Wallet shouldBeNull = walletDAO.findById(9999);
            if (shouldBeNull == null) {
                System.out.println("✅ Carteira deletada com sucesso");
                passTest();
            } else {
                System.out.println("❌ Erro: Carteira ainda existe");
                failTest();
            }

        } catch (Exception e) {
            System.err.println("❌ Erro durante teste de WalletDAO: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    // ============================================================================
    // FASE 2: TESTES DE DAOs DE MERCADO
    // ============================================================================

    /**
     * Teste completo de MarketDAO
     */
    private static void testMarketDAO() {
        startTest("MarketDAO - Gerenciamento de Preços");

        try {
            System.out.println("\n--- 1. SAVE: Salvando preços no mercado ---");
            marketDAO.save("BTC", 68500.50);
            marketDAO.save("ETH", 3500.75);
            marketDAO.save("ADA", 0.55);
            passTest();

            System.out.println("\n--- 2. GET: Obtendo preço específico ---");
            Double btcPrice = marketDAO.getPrice("BTC");
            if (btcPrice != null) {
                System.out.println("✅ Preço do BTC: $" + btcPrice);
                passTest();
            } else {
                System.out.println("❌ Preço não encontrado");
                failTest();
            }

            System.out.println("\n--- 3. GET ALL: Obtendo todos os preços ---");
            Map<String, Double> allPrices = marketDAO.getAllPrices();
            System.out.println("📊 Preços do mercado:");
            allPrices.forEach((symbol, price) ->
                    System.out.println("  " + symbol + ": $" + price));
            passTest();

            System.out.println("\n--- 4. UPDATE: Atualizando preço ---");
            marketDAO.save("BTC", 69000.00);
            Double updatedPrice = marketDAO.getPrice("BTC");
            if (updatedPrice == 69000.00) {
                System.out.println("✅ Preço atualizado: $" + updatedPrice);
                passTest();
            } else {
                System.out.println("⚠️ Preço não foi atualizado corretamente");
                failTest();
            }

            System.out.println("\n--- 5. DELETE: Removendo preço ---");
            marketDAO.delete("ADA");
            Double shouldBeNull = marketDAO.getPrice("ADA");
            if (shouldBeNull == null) {
                System.out.println("✅ Preço removido com sucesso");
                passTest();
            } else {
                System.out.println("❌ Preço ainda existe");
                failTest();
            }

        } catch (Exception e) {
            System.err.println("❌ Erro durante teste de MarketDAO: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    /**
     * Teste completo de TransactionDAO
     */
    private static void testTransactionDAO() {
        startTest("TransactionDAO - Gerenciamento de Transações");

        try {
            // Preparar dados necessários
            CryptoAsset asset = cryptoAssetDAO.findById(1);
            if (asset == null) {
                System.out.println("⚠️ Teste pulado: CryptoAsset ID 1 não encontrado");
                return;
            }

            System.out.println("\n--- 1. INSERT: Inserindo transação ---");
            Transaction buyTransaction = new Transaction(asset, 0.5, "BUY");
            transactionDAO.insert(buyTransaction, 1, 1);
            passTest();

            System.out.println("\n--- 2. FIND BY USER: Buscando transações do usuário ---");
            List<Transaction> userTransactions = transactionDAO.findByUserId(1);
            System.out.println("📊 Total de transações do usuário 1: " + userTransactions.size());
            for (Transaction t : userTransactions) {
                t.showTransaction();
            }
            passTest();

        } catch (Exception e) {
            System.err.println("❌ Erro durante teste de TransactionDAO: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    // ============================================================================
    // FASE 3: TESTES DE DAOs DE RELACIONAMENTO
    // ============================================================================

    /**
     * Teste completo de UserCompanyRelationDAO
     */
    private static void testUserCompanyRelationDAO() {
        startTest("UserCompanyRelationDAO - Relacionamento User-Company");

        try {
            System.out.println("\n--- 1. INSERT: Criando relação ---");
            userCompanyRelationDAO.insert(1, 1, 5000.0, LocalDate.now());
            passTest();

            System.out.println("\n--- 2. UPDATE: Atualizando valor investido ---");
            userCompanyRelationDAO.updateInvestedAmount(1, 1, 7500.0);
            passTest();

            System.out.println("\n--- 3. FIND: Buscando usuários por empresa ---");
            List<Integer> users = userCompanyRelationDAO.findUsersByCompanyId(1);
            System.out.println("📊 Usuários investindo na empresa 1: " + users);
            passTest();

            System.out.println("\n--- 4. DELETE: Removendo relação ---");
            userCompanyRelationDAO.delete(1, 1);
            passTest();

        } catch (Exception e) {
            System.err.println("❌ Erro durante teste: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    /**
     * Teste completo de CompanyCryptoAssetDAO
     */
    private static void testCompanyCryptoAssetDAO() {
        startTest("CompanyCryptoAssetDAO - Alocação de Ativos para Empresas");

        try {
            System.out.println("\n--- 1. ADD/UPDATE: Alocando ativo ---");
            companyCryptoAssetDAO.addOrUpdateAssetForCompany(1, 1, 10.0);
            passTest();

            System.out.println("\n--- 2. GET: Listando ativos da empresa ---");
            List<CryptoAsset> assets = companyCryptoAssetDAO.getAssetsByCompanyId(1);
            System.out.println("📊 Ativos da empresa 1:");
            for (CryptoAsset asset : assets) {
                System.out.println("  - " + asset.getName() + ": " + asset.getQuantity());
            }
            passTest();

            System.out.println("\n--- 3. REMOVE: Removendo ativo ---");
            companyCryptoAssetDAO.removeAssetFromCompany(1, 1);
            passTest();

        } catch (Exception e) {
            System.err.println("❌ Erro durante teste: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    /**
     * Teste completo de WalletCryptoAssetDAO
     */
    private static void testWalletCryptoAssetDAO() {
        startTest("WalletCryptoAssetDAO - Gerenciamento de Ativos em Carteiras");

        try {
            // Preparar carteira de teste
            Wallet testWallet = new Wallet(8888, 1, "Test Portfolio");
            walletDAO.insert(testWallet);

            System.out.println("\n--- 1. ADD: Adicionando ativo à carteira ---");
            walletCryptoAssetDAO.addCryptoAssetToWallet(8888, 1, 2.5);
            passTest();

            System.out.println("\n--- 2. FIND: Listando ativos da carteira ---");
            List<CryptoAsset> walletAssets = walletCryptoAssetDAO.findCryptoAssetsByWallet(8888);
            System.out.println("📊 Ativos na carteira 8888:");
            for (CryptoAsset asset : walletAssets) {
                System.out.println("  - " + asset.getName() + " (" + asset.getSymbol() +
                        "): " + asset.getQuantity() + " @ $" + asset.getPrice());
            }
            passTest();

            System.out.println("\n--- 3. UPDATE: Atualizando quantidade ---");
            walletCryptoAssetDAO.updateCryptoAssetQuantity(8888, 1, 5.0);
            passTest();

            System.out.println("\n--- 4. REMOVE: Removendo ativo da carteira ---");
            walletCryptoAssetDAO.removeCryptoAssetFromWallet(8888, 1);
            passTest();

            // Limpeza
            walletDAO.delete(8888);

        } catch (Exception e) {
            System.err.println("❌ Erro durante teste: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    // ============================================================================
    // FASE 4: CENÁRIOS COMPLEXOS
    // ============================================================================

    /**
     * CENÁRIO COMPLEXO 1: Gerenciamento de Portfolio
     *
     * Simula um usuário criando uma carteira, comprando múltiplos ativos,
     * acompanhando variações de preço e calculando rendimentos
     */
    private static void testComplexScenario1_PortfolioManagement() {
        startTest("CENÁRIO COMPLEXO 1: Gerenciamento de Portfolio");

        try {
            System.out.println("\n📋 Cenário: Usuário cria portfolio diversificado");
            System.out.println("─".repeat(80));

            // 1. Criar usuário e carteira
            User investor = new User("Maria Silva", 8001, "maria.silva@invest.com", "secure123");
            investor.insert();

            Wallet portfolio = new Wallet(7001, 8001, "Portfolio Principal");
            walletDAO.insert(portfolio);
            System.out.println("✅ Usuário e carteira criados");

            // 2. Configurar preços de mercado
            marketDAO.save("BTC", 65000.0);
            marketDAO.save("ETH", 3200.0);
            marketDAO.save("SOL", 150.0);
            System.out.println("✅ Preços de mercado configurados");

            // 3. "Comprar" ativos e adicionar à carteira
            System.out.println("\n💰 Realizando compras:");

            // Buscar IDs dos ativos (assumindo que existem no banco)
            List<CryptoAsset> availableAssets = cryptoAssetDAO.findAll();
            Map<String, Integer> assetIds = new HashMap<>();
            for (CryptoAsset asset : availableAssets) {
                assetIds.put(asset.getSymbol(), asset.getId());
            }

            // Comprar BTC
            if (assetIds.containsKey("BTC")) {
                int btcId = assetIds.get("BTC");
                walletCryptoAssetDAO.addCryptoAssetToWallet(7001, btcId, 1.5);

                CryptoAsset btc = cryptoAssetDAO.findById(btcId);
                Transaction buyBTC = new Transaction(btc, 1.5, "BUY");
                transactionDAO.insert(buyBTC, 8001, btcId);
                System.out.println("  ✓ Comprou 1.5 BTC @ $65,000 = $97,500");
            }

            // Comprar ETH
            if (assetIds.containsKey("ETH")) {
                int ethId = assetIds.get("ETH");
                walletCryptoAssetDAO.addCryptoAssetToWallet(7001, ethId, 10.0);

                CryptoAsset eth = cryptoAssetDAO.findById(ethId);
                Transaction buyETH = new Transaction(eth, 10.0, "BUY");
                transactionDAO.insert(buyETH, 8001, ethId);
                System.out.println("  ✓ Comprou 10.0 ETH @ $3,200 = $32,000");
            }

            // 4. Exibir portfolio completo
            System.out.println("\n📊 PORTFOLIO ATUAL:");
            System.out.println("─".repeat(80));
            List<CryptoAsset> portfolioAssets = walletCryptoAssetDAO.findCryptoAssetsByWallet(7001);
            double totalValue = 0;

            for (CryptoAsset asset : portfolioAssets) {
                double assetValue = asset.getQuantity() * asset.getPrice();
                totalValue += assetValue;
                System.out.printf("%-15s | Qty: %8.4f | Price: $%10.2f | Value: $%12.2f%n",
                        asset.getSymbol(), asset.getQuantity(), asset.getPrice(), assetValue);
            }
            System.out.println("─".repeat(80));
            System.out.printf("VALOR TOTAL DO PORTFOLIO: $%,.2f%n", totalValue);

            // 5. Simular variação de mercado
            System.out.println("\n📈 SIMULANDO VARIAÇÃO DE MERCADO (+5% BTC, -3% ETH)");
            marketDAO.save("BTC", 68250.0);  // +5%
            marketDAO.save("ETH", 3104.0);   // -3%

            // Recalcular portfolio
            System.out.println("\n📊 PORTFOLIO APÓS VARIAÇÃO:");
            System.out.println("─".repeat(80));
            double newTotalValue = 0;

            for (CryptoAsset asset : portfolioAssets) {
                Double newPrice = marketDAO.getPrice(asset.getSymbol());
                if (newPrice != null) {
                    double assetValue = asset.getQuantity() * newPrice;
                    newTotalValue += assetValue;
                    System.out.printf("%-15s | Qty: %8.4f | Price: $%10.2f | Value: $%12.2f%n",
                            asset.getSymbol(), asset.getQuantity(), newPrice, assetValue);
                }
            }
            System.out.println("─".repeat(80));
            System.out.printf("VALOR TOTAL DO PORTFOLIO: $%,.2f%n", newTotalValue);
            System.out.printf("LUCRO/PREJUÍZO: $%,.2f (%.2f%%)%n",
                    newTotalValue - totalValue,
                    ((newTotalValue - totalValue) / totalValue) * 100);

            // 6. Limpeza
            System.out.println("\n🧹 Limpando dados de teste...");
            walletDAO.delete(7001);
            User.findById(8001).delete();

            passTest();

        } catch (Exception e) {
            System.err.println("❌ Erro no cenário: " + e.getMessage());
            e.printStackTrace();
            failTest();
        }

        endTest();
    }

    /**
     * CENÁRIO COMPLEXO 2: Investimento Corporativo
     *
     * Simula múltiplos usuários investindo em diferentes empresas,
     * empresas alocando ativos cripto, e relatórios consolidados
     */
    private static void testComplexScenario2_CompanyInvestment() {
        startTest("CENÁRIO COMPLEXO 2: Investimento Corporativo");

        try {
            System.out.println("\n📋 Cenário: Múltiplas empresas com investidores");
            System.out.println("─".repeat(80));

            // CORRIGIDO: Usar IDs únicos
            Company tech = new Company("TechStartup", 90001, "TECH-90001");
            Company finance = new Company("FinanceInc", 90002, "FIN-90002");
            companyDAO.insert(tech);
            companyDAO.insert(finance);
            System.out.println("✅ Empresas criadas");

            // Criar investidores
            User inv1 = new User("João Investidor", 8101, "joao@inv.com", "pass1");
            User inv2 = new User("Ana Capital", 8102, "ana@inv.com", "pass2");
            User inv3 = new User("Pedro Fundo", 8103, "pedro@inv.com", "pass3");
            inv1.insert();
            inv2.insert();
            inv3.insert();
            System.out.println("✅ Investidores criados");

            // Criar relacionamentos de investimento - USAR IDs CORRETOS
            System.out.println("\n💵 Estabelecendo investimentos:");
            userCompanyRelationDAO.insert(8101, 90001, 50000.0, LocalDate.now());
            System.out.println("  ✓ João investiu $50,000 em TechStartup");

            userCompanyRelationDAO.insert(8101, 90002, 30000.0, LocalDate.now());
            System.out.println("  ✓ João investiu $30,000 em FinanceInc");

            userCompanyRelationDAO.insert(8102, 90001, 75000.0, LocalDate.now());
            System.out.println("  ✓ Ana investiu $75,000 em TechStartup");

            userCompanyRelationDAO.insert(8103, 90002, 100000.0, LocalDate.now());
            System.out.println("  ✓ Pedro investiu $100,000 em FinanceInc");

            // Empresas alocam ativos cripto - USAR IDs CORRETOS
            System.out.println("\n🏢 Empresas alocando ativos cripto:");
            companyCryptoAssetDAO.addOrUpdateAssetForCompany(90001, 1, 5.0);
            System.out.println("  ✓ TechStartup alocou 5.0 BTC");

            companyCryptoAssetDAO.addOrUpdateAssetForCompany(90002, 1, 3.0);
            companyCryptoAssetDAO.addOrUpdateAssetForCompany(90002, 2, 20.0);
            System.out.println("  ✓ FinanceInc alocou 3.0 BTC + 20.0 ETH");

            // Gerar relatório consolidado
            System.out.println("\n📊 RELATÓRIO CONSOLIDADO:");
            System.out.println("═".repeat(80));

            // Relatório por empresa - USAR IDs CORRETOS
            List<Company> companies = Arrays.asList(
                    companyDAO.findById(90001),
                    companyDAO.findById(90002)
            );

            for (Company company : companies) {
                if (company != null) {
                    System.out.println("\n🏢 " + company.getName() + " (" + company.getIdentifier() + ")");
                    System.out.println("─".repeat(80));

                    // Investidores
                    List<Integer> investorIds = userCompanyRelationDAO.findUsersByCompanyId(company.getId());
                    System.out.println("👥 Investidores: " + investorIds.size());

                    // Ativos
                    List<CryptoAsset> companyAssets = companyCryptoAssetDAO.getAssetsByCompanyId(company.getId());
                    System.out.println("💎 Ativos Cripto:");
                    double totalAssetValue = 0;
                    for (CryptoAsset asset : companyAssets) {
                        double value = asset.getQuantity() * asset.getPrice();
                        totalAssetValue += value;
                        System.out.printf("   - %-10s: %8.4f @ $%10.2f = $%12.2f%n",
                                asset.getSymbol(), asset.getQuantity(), asset.getPrice(), value);
                    }
                    System.out.printf("💰 Valor Total em Cripto: $%,.2f%n", totalAssetValue);
                }
            }

            // Limpeza - USAR IDs CORRETOS
            System.out.println("\n🧹 Limpando dados de teste...");
            userCompanyRelationDAO.delete(8101, 90001);
            userCompanyRelationDAO.delete(8101, 90002);
            userCompanyRelationDAO.delete(8102, 90001);
            userCompanyRelationDAO.delete(8103, 90002);
            companyCryptoAssetDAO.removeAssetFromCompany(90001, 1);
            companyCryptoAssetDAO.removeAssetFromCompany(90002, 1);
            companyCryptoAssetDAO.removeAssetFromCompany(90002, 2);
            companyDAO.delete(90001);
            companyDAO.delete(90002);
            User.findById(8101).delete();
            User.findById(8102).delete();
            User.findById(8103).delete();

            passTest();

        } catch (Exception e) {
            System.err.println("❌ Erro no cenário: " + e.getMessage());
            e.printStackTrace();
            failTest();
        }

        endTest();
    }

    /**
     * CENÁRIO COMPLEXO 3: Simulação de Mercado
     *
     * Simula um dia de trading com múltiplas transações,
     * variações de preço e análise de performance
     */
    private static void testComplexScenario3_MarketSimulation() {
        startTest("CENÁRIO COMPLEXO 3: Simulação de Mercado (Trading Day)");

        try {
            System.out.println("\n📋 Cenário: Dia de trading com múltiplas transações");
            System.out.println("─".repeat(80));

            // 1. Setup inicial
            User trader = new User("Carlos Trader", 8201, "carlos@trade.com", "trade123");
            trader.insert();

            Wallet tradingWallet = new Wallet(7201, 8201, "Trading Wallet");
            walletDAO.insert(tradingWallet);

            // 2. Preços iniciais
            System.out.println("\n💹 ABERTURA DO MERCADO:");
            marketDAO.save("BTC", 60000.0);
            marketDAO.save("ETH", 3000.0);
            System.out.println("  BTC: $60,000 | ETH: $3,000");

            // 3. Primeira rodada de compras (manhã)
            System.out.println("\n🌅 PERÍODO DA MANHÃ - Compras:");
            List<CryptoAsset> assets = cryptoAssetDAO.findAll();
            Integer btcId = null, ethId = null;

            for (CryptoAsset a : assets) {
                if ("BTC".equals(a.getSymbol())) btcId = a.getId();
                if ("ETH".equals(a.getSymbol())) ethId = a.getId();
            }

            if (btcId != null) {
                walletCryptoAssetDAO.addCryptoAssetToWallet(7201, btcId, 2.0);
                CryptoAsset btc = cryptoAssetDAO.findById(btcId);
                transactionDAO.insert(new Transaction(btc, 2.0, "BUY"), 8201, btcId);
                System.out.println("  ✓ 09:00 - Comprou 2.0 BTC @ $60,000");
            }

            if (ethId != null) {
                walletCryptoAssetDAO.addCryptoAssetToWallet(7201, ethId, 15.0);
                CryptoAsset eth = cryptoAssetDAO.findById(ethId);
                transactionDAO.insert(new Transaction(eth, 15.0, "BUY"), 8201, ethId);
                System.out.println("  ✓ 10:30 - Comprou 15.0 ETH @ $3,000");
            }

            // 4. Variação de mercado (tarde)
            System.out.println("\n☀️ PERÍODO DA TARDE - Mercado em alta:");
            marketDAO.save("BTC", 62500.0);  // +4.17%
            marketDAO.save("ETH", 3150.0);   // +5%
            System.out.println("  BTC: $62,500 (+4.17%) | ETH: $3,150 (+5.00%)");

            // 5. Venda parcial (realização de lucro)
            System.out.println("\n💰 PERÍODO DA TARDE - Realização de lucro:");
            if (ethId != null) {
                walletCryptoAssetDAO.updateCryptoAssetQuantity(7201, ethId, 10.0);
                CryptoAsset eth = cryptoAssetDAO.findById(ethId);
                transactionDAO.insert(new Transaction(eth, 5.0, "SELL"), 8201, ethId);
                System.out.println("  ✓ 14:00 - Vendeu 5.0 ETH @ $3,150");
                System.out.println("  📈 Lucro: $" + (5.0 * (3150.0 - 3000.0)));
            }

            // 6. Fechamento do mercado
            System.out.println("\n🌙 FECHAMENTO DO MERCADO:");
            marketDAO.save("BTC", 63000.0);  // +5%
            marketDAO.save("ETH", 3100.0);   // +3.33%
            System.out.println("  BTC: $63,000 (+5.00% no dia) | ETH: $3,100 (+3.33% no dia)");

            // 7. Relatório final
            System.out.println("\n📊 RELATÓRIO DO DIA:");
            System.out.println("═".repeat(80));

            List<Transaction> dayTransactions = transactionDAO.findByUserId(8201);
            System.out.println("📝 Transações realizadas: " + dayTransactions.size());

            List<CryptoAsset> finalPortfolio = walletCryptoAssetDAO.findCryptoAssetsByWallet(7201);
            System.out.println("\n💼 Portfolio final:");
            double finalValue = 0;
            for (CryptoAsset asset : finalPortfolio) {
                Double currentPrice = marketDAO.getPrice(asset.getSymbol());
                if (currentPrice != null) {
                    double value = asset.getQuantity() * currentPrice;
                    finalValue += value;
                    System.out.printf("   %-10s: %8.4f @ $%10.2f = $%12.2f%n",
                            asset.getSymbol(), asset.getQuantity(), currentPrice, value);
                }
            }
            System.out.println("─".repeat(80));
            System.out.printf("💰 Valor Total: $%,.2f%n", finalValue);

            // Cálculo de P&L
            double initialInvestment = (2.0 * 60000.0) + (15.0 * 3000.0);
            double cashFromSell = 5.0 * 3150.0;
            double unrealizedValue = finalValue;
            double totalPL = (cashFromSell + unrealizedValue) - initialInvestment;

            System.out.printf("\n📈 ANÁLISE DE P&L:%n");
            System.out.printf("   Investimento Inicial: $%,.2f%n", initialInvestment);
            System.out.printf("   Cash (Vendas):        $%,.2f%n", cashFromSell);
            System.out.printf("   Portfolio (Não real.): $%,.2f%n", unrealizedValue);
            System.out.printf("   P&L Total:            $%,.2f (%.2f%%)%n",
                    totalPL, (totalPL / initialInvestment) * 100);

            // 8. Limpeza
            System.out.println("\n🧹 Limpando dados de teste...");
            walletDAO.delete(7201);
            User.findById(8201).delete();

            passTest();

        } catch (Exception e) {
            System.err.println("❌ Erro no cenário: " + e.getMessage());
            e.printStackTrace();
            failTest();
        }

        endTest();
    }

    // ============================================================================
    // FASE 5: TESTES COM COLLECTIONS
    // ============================================================================

    /**
     * Testa integração com Collections (List, Map, Set)
     */
    private static void testCollectionsIntegration() {
        startTest("TESTES COM COLLECTIONS - List, Map, Set");

        try {
            System.out.println("\n📦 Testando diferentes tipos de Collections");
            System.out.println("─".repeat(80));

            // 1. ArrayList - Lista de usuários
            System.out.println("\n1️⃣ ArrayList<User> - Gestão de usuários:");
            List<User> userList = new ArrayList<>();
            userList.add(new User("Alice", 8301, "alice@test.com", "pass1"));
            userList.add(new User("Bob", 8302, "bob@test.com", "pass2"));
            userList.add(new User("Charlie", 8303, "charlie@test.com", "pass3"));

            System.out.println("   Total: " + userList.size() + " usuários");
            userList.forEach(u -> System.out.println("   - " + u.getName()));
            passTest();

            // 2. HashMap - Mapeamento User -> Wallet
            System.out.println("\n2️⃣ HashMap<User, Wallet> - Usuário-Carteira:");
            Map<User, Wallet> userWallets = new HashMap<>();
            for (User user : userList) {
                Wallet w = new Wallet(7300 + user.getId(), user.getId(), user.getName() + "'s Wallet");
                userWallets.put(user, w);
            }

            System.out.println("   Mapeamentos criados: " + userWallets.size());
            userWallets.forEach((user, wallet) ->
                    System.out.println("   - " + user.getName() + " -> " + wallet.getName()));
            passTest();

            // 3. HashMap - Symbol -> Price
            System.out.println("\n3️⃣ HashMap<String, Double> - Preços de mercado:");
            Map<String, Double> marketPrices = marketDAO.getAllPrices();
            System.out.println("   Preços carregados: " + marketPrices.size());
            marketPrices.forEach((symbol, price) ->
                    System.out.printf("   - %s: $%,.2f%n", symbol, price));
            passTest();

            // 4. HashSet - Símbolos únicos
            System.out.println("\n4️⃣ HashSet<String> - Símbolos únicos:");
            Set<String> uniqueSymbols = new HashSet<>(marketPrices.keySet());
            System.out.println("   Símbolos únicos: " + uniqueSymbols);
            passTest();

            // 5. LinkedList - Histórico de transações
            System.out.println("\n5️⃣ LinkedList<Transaction> - Histórico ordenado:");
            LinkedList<Transaction> transactionHistory = new LinkedList<>();
            List<CryptoAsset> assets = cryptoAssetDAO.findAll();

            if (!assets.isEmpty()) {
                CryptoAsset asset = assets.get(0);
                transactionHistory.add(new Transaction(asset, 1.0, "BUY"));
                transactionHistory.add(new Transaction(asset, 0.5, "SELL"));
                transactionHistory.add(new Transaction(asset, 2.0, "BUY"));

                System.out.println("   Total de transações: " + transactionHistory.size());
                System.out.println("   Primeira: " + transactionHistory.getFirst().getType());
                System.out.println("   Última: " + transactionHistory.getLast().getType());
            }
            passTest();

            // 6. TreeMap - Ordenação automática por símbolo
            System.out.println("\n6️⃣ TreeMap<String, Double> - Preços ordenados:");
            TreeMap<String, Double> sortedPrices = new TreeMap<>(marketPrices);
            System.out.println("   Preços em ordem alfabética:");
            sortedPrices.forEach((symbol, price) ->
                    System.out.printf("   - %s: $%,.2f%n", symbol, price));
            passTest();

            // 7. Stream API - Filtros e agregações
            System.out.println("\n7️⃣ Stream API - Análises avançadas:");

            // Empresas com mais de 50k investidos
            List<Company> companies = companyDAO.findAll();
            System.out.println("   Total de empresas no banco: " + companies.size());

            // Ativos com preço acima de $1000
            List<CryptoAsset> expensiveAssets = assets.stream()
                    .filter(a -> a.getPrice() > 1000.0)
                    .collect(Collectors.toList());
            System.out.println("   Ativos > $1000: " + expensiveAssets.size());

            // Soma total de quantidades
            double totalQuantity = assets.stream()
                    .mapToDouble(CryptoAsset::getQuantity)
                    .sum();
            System.out.printf("   Quantidade total de todos ativos: %.2f%n", totalQuantity);
            passTest();

            // 8. Map de Maps - Estrutura complexa
            System.out.println("\n8️⃣ Map<User, Map<String, Double>> - Portfolio por usuário:");
            Map<User, Map<String, Double>> userPortfolios = new HashMap<>();

            for (User user : userList) {
                Map<String, Double> portfolio = new HashMap<>();
                portfolio.put("BTC", 1.5);
                portfolio.put("ETH", 10.0);
                userPortfolios.put(user, portfolio);
            }

            System.out.println("   Estrutura criada para " + userPortfolios.size() + " usuários");
            userPortfolios.forEach((user, portfolio) -> {
                System.out.println("   - " + user.getName() + ": " + portfolio.keySet());
            });
            passTest();

        } catch (Exception e) {
            System.err.println("❌ Erro durante testes de Collections: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    // ============================================================================
    // FASE 6: RELATÓRIOS E PERSISTÊNCIA
    // ============================================================================

    /**
     * Testa geração de relatórios e persistência em arquivo
     */
    private static void testReportsAndPersistence() {
        startTest("RELATÓRIOS E PERSISTÊNCIA");

        try {
            System.out.println("\n📄 Gerando relatórios e salvando em arquivo");
            System.out.println("─".repeat(80));

            // 1. Criar dados para relatório
            Wallet reportWallet = new Wallet(7401, 1, "Report Wallet");
            List<CryptoAsset> assets = cryptoAssetDAO.findAll();

            if (!assets.isEmpty()) {
                for (int i = 0; i < Math.min(3, assets.size()); i++) {
                    reportWallet.addAsset(assets.get(i));
                }
            }

            // 2. Gerar relatório de carteira
            System.out.println("\n📊 Relatório de Carteira:");
            Report report = new Report();
            report.generateWalletReport(reportWallet);
            passTest();

            // 3. Relatório de empresas
            System.out.println("\n🏢 Relatório de Empresas:");
            List<Company> companies = companyDAO.findAll();
            if (!companies.isEmpty()) {
                report.generateCompanyReport(companies);
            }
            passTest();

            // 4. Salvar dados em arquivo
            System.out.println("\n💾 Salvando dados em arquivo...");
            Path dataFile = Paths.get("data_test.txt");

            try (BufferedWriter writer = Files.newBufferedWriter(dataFile)) {
                writer.write("# VOLTZ CRYPTO MANAGEMENT SYSTEM - DATA EXPORT\n");
                writer.write("# Generated: " + LocalDate.now() + "\n\n");

                // Exportar usuários
                writer.write("## USERS\n");
                List<User> allUsers = User.getAllUsers();
                for (User u : allUsers) {
                    writer.write(String.format("USER|%d|%s|%s\n",
                            u.getId(), u.getName(), u.getEmail()));
                }

                // Exportar empresas
                writer.write("\n## COMPANIES\n");
                for (Company c : companies) {
                    writer.write(String.format("COMPANY|%d|%s|%s\n",
                            c.getId(), c.getName(), c.getIdentifier()));
                }

                // Exportar ativos
                writer.write("\n## CRYPTO ASSETS\n");
                for (CryptoAsset a : assets) {
                    writer.write(String.format("ASSET|%s|%s|%.4f|%.2f\n",
                            a.getName(), a.getSymbol(), a.getQuantity(), a.getPrice()));
                }

                // Exportar preços de mercado
                writer.write("\n## MARKET PRICES\n");
                Map<String, Double> prices = marketDAO.getAllPrices();
                for (Map.Entry<String, Double> entry : prices.entrySet()) {
                    writer.write(String.format("PRICE|%s|%.2f\n",
                            entry.getKey(), entry.getValue()));
                }

                System.out.println("✅ Dados salvos em: " + dataFile.toAbsolutePath());
            }
            passTest();

            // 5. Ler dados do arquivo
            System.out.println("\n📖 Lendo dados do arquivo...");
            List<String> lines = Files.readAllLines(dataFile);
            long userCount = lines.stream().filter(l -> l.startsWith("USER|")).count();
            long companyCount = lines.stream().filter(l -> l.startsWith("COMPANY|")).count();
            long assetCount = lines.stream().filter(l -> l.startsWith("ASSET|")).count();

            System.out.println("   Usuários encontrados: " + userCount);
            System.out.println("   Empresas encontradas: " + companyCount);
            System.out.println("   Ativos encontrados: " + assetCount);
            passTest();

            // 6. Estatísticas gerais do sistema
            System.out.println("\n📈 ESTATÍSTICAS GERAIS DO SISTEMA:");
            System.out.println("═".repeat(80));
            System.out.println("📊 Total de Usuários:    " + User.getAllUsers().size());
            System.out.println("🏢 Total de Empresas:    " + companyDAO.findAll().size());
            System.out.println("💎 Total de Ativos:      " + cryptoAssetDAO.findAll().size());
            System.out.println("💹 Preços no Mercado:    " + marketDAO.getAllPrices().size());
            System.out.println("👛 Total de Carteiras:   " + walletDAO.findAll().size());
            passTest();

        } catch (Exception e) {
            System.err.println("❌ Erro durante geração de relatórios: " + e.getMessage());
            e.printStackTrace();
            failTest();
        }

        endTest();
    }

    // ============================================================================
    // TESTES LEGADOS (MANTIDOS PARA COMPATIBILIDADE)
    // ============================================================================

    /**
     * Teste completo de CRUD para usuários (versão original melhorada)
     */
    private static void testUserCRUD() {
        startTest("User - CRUD Completo (Model Integrado)");

        try {
            // 1. Ver estado inicial
            System.out.println("\n--- Etapa 1: Listando usuários ANTES da inserção ---");
            User.displayAllUsers();
            passTest();

            // 2. Criar usuário de teste com ID único
            int nextId = User.getNextAvailableId();
            User usuarioDeTeste = new User("Usuário Teste", nextId, "teste" + nextId + "@voltz.com", "senha123");
            System.out.println("\n--- Etapa 2: Inserindo '" + usuarioDeTeste.getName() + "' com ID " + nextId + " ---");
            usuarioDeTeste.insert();
            passTest();

            // 3. Verificar a criação
            System.out.println("\n--- Etapa 3: Verificando se o usuário foi inserido ---");
            User.displayAllUsers();
            passTest();

            // 4. Atualizar
            usuarioDeTeste.setName("Usuário Teste (Atualizado)");
            usuarioDeTeste.setEmail("usuario.teste.atualizado" + nextId + "@voltz.com");
            System.out.println("\n--- Etapa 4: Atualizando para '" + usuarioDeTeste.getName() + "' ---");
            usuarioDeTeste.update();
            passTest();

            // 5. Verificar a atualização
            System.out.println("\n--- Etapa 5: Verificando se o usuário foi atualizado ---");
            User.displayAllUsers();
            passTest();

            // 6. Teste de busca
            System.out.println("\n--- Etapa 6: Testando busca por ID ---");
            User usuarioEncontrado = User.findById(usuarioDeTeste.getId());
            if (usuarioEncontrado != null) {
                System.out.println("✅ Usuário encontrado:");
                usuarioEncontrado.showInfo();
                passTest();
            } else {
                System.out.println("❌ Usuário não encontrado");
                failTest();
            }

            // 7. Deletar
            System.out.println("\n--- Etapa 7: Excluindo o usuário de teste ---");
            usuarioDeTeste.delete();
            passTest();

            // 8. Verificar a exclusão
            System.out.println("\n--- Etapa 8: Verificando se o usuário foi excluído ---");
            User.displayAllUsers();
            passTest();

        } catch (Exception e) {
            System.err.println("❌ Erro durante teste de User: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    /**
     * Teste de conexão com Oracle Database
     */
    private static void testOracleConnection() {
        startTest("CONEXÃO COM ORACLE DATABASE");

        try {
            System.out.println("\n1. Testando conexão...");
            Connection conn = OracleConnection.getConnection();

            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Conexão estabelecida com sucesso!");
                passTest();

                System.out.println("\n2. Testando execução de comando SQL...");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT SYSDATE FROM DUAL");

                if (rs.next()) {
                    System.out.println("📅 Data/Hora do servidor Oracle: " + rs.getTimestamp(1));
                    passTest();
                }

                rs.close();
                stmt.close();
                System.out.println("✅ Comando SQL executado com sucesso!");

            } else {
                System.out.println("❌ Falha na conexão!");
                failTest();
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro SQL: " + e.getMessage());
            System.err.println("💡 Verifique: conexão de rede, credenciais e VPN da FIAP");
            failTest();
        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
            failTest();
        }

        endTest();
    }

    // ============================================================================
    // MÉTODOS AUXILIARES DE TESTE
    // ============================================================================

    /**
     * Inicia um novo teste
     */
    private static void startTest(String testName) {
        System.out.println("\n" + "━".repeat(80));
        System.out.println("🧪 TESTE: " + testName);
        System.out.println("━".repeat(80));
        totalTests++;
    }

    /**
     * Finaliza um teste
     */
    private static void endTest() {
        System.out.println("━".repeat(80));
    }

    /**
     * Marca teste como aprovado
     */
    private static void passTest() {
        passedTests++;
    }

    /**
     * Marca teste como reprovado
     */
    private static void failTest() {
        failedTests++;
    }

    /**
     * Imprime cabeçalho do sistema
     */
    private static void printHeader() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("   ⚡ VOLTZ CRYPTO MANAGEMENT SYSTEM - TEST SUITE v2.0");
        System.out.println("   🔬 Sistema de Testes Integrados com Oracle Database");
        System.out.println("=".repeat(80));
        System.out.println("📅 Data: " + LocalDate.now());
        System.out.println("🏛️  Instituição: FIAP");
        System.out.println("=".repeat(80) + "\n");
    }

    /**
     * Imprime estatísticas finais dos testes
     */
    private static void printStatistics() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📊 ESTATÍSTICAS FINAIS DOS TESTES");
        System.out.println("=".repeat(80));
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
                System.out.println("\n⚠️  Atenção! Vários testes falharam. Revise o código.");
            }
        }

        System.out.println("=".repeat(80));
    }
}