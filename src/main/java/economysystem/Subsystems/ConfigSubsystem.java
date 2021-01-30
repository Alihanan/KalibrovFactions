package economysystem.Subsystems;

import economysystem.MedievalEconomy;

import java.io.File;

public class ConfigSubsystem {

    MedievalEconomy medievalEconomy = null;

    public ConfigSubsystem(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void handleVersionMismatch() {

        if (!medievalEconomy.getConfig().getString("version").equalsIgnoreCase(medievalEconomy.version)) {
            System.out.println("[ALERT] Verson mismatch! Saving old config as config.yml.old and loading in the default values.");
            renameConfigToConfigDotOldAndSaveDefaults();
        }

    }

    public void renameConfigToConfigDotOldAndSaveDefaults() {
        // save old config as config.yml.old
        File saveFile = new File("./plugins/MedievalEconomy/config.yml");
        if (saveFile.exists()) {

            // rename file
            File newSaveFile = new File("./plugins/MedievalEconomy/config.yml.old");
            saveFile.renameTo(newSaveFile);

            // save defaults
            saveConfigDefaults();
        }
    }

    public void saveConfigDefaults() {
        medievalEconomy.getConfig().addDefault("version", medievalEconomy.version);
        medievalEconomy.getConfig().addDefault("enablingText", "Medieval Economy is enabling...");
        medievalEconomy.getConfig().addDefault("enabledText", "Medieval Economy is enabled!");
        medievalEconomy.getConfig().addDefault("disablingText", "Medieval Economy is disabling...");
        medievalEconomy.getConfig().addDefault("disabledText", "Medieval Economy is disabled!");
        medievalEconomy.getConfig().addDefault("coinpurseSaveErrorText", "An error occurred saving a Coinpurse Record.");
        medievalEconomy.getConfig().addDefault("coinpurseLoadErrorText", "An error occurred loading ");
        medievalEconomy.getConfig().addDefault("balanceTextStart", "You have ");
        medievalEconomy.getConfig().addDefault("balanceTextEnd", " coins in your coinpurse.");
        medievalEconomy.getConfig().addDefault("balanceNoPermission", "Sorry! In order to run this command, you need the following permission: 'medievaleconomy.balance'");
        medievalEconomy.getConfig().addDefault("depositUsageText", "Usage: /deposit (whole number)");
        medievalEconomy.getConfig().addDefault("depositPositiveText", "Number must be positive!");
        medievalEconomy.getConfig().addDefault("depositTextStart", "You open your coinpurse and deposit ");
        medievalEconomy.getConfig().addDefault("depositTextEnd", " coins.");
        medievalEconomy.getConfig().addDefault("depositNotEnoughCoins", "You don't have that many coins!");
        medievalEconomy.getConfig().addDefault("depositUsageText", "Usage: /deposit (number)");
        medievalEconomy.getConfig().addDefault("depositNoPermission", "Sorry! In order to use this command, you need the permission 'medievaleconomy.deposit'");
        medievalEconomy.getConfig().addDefault("createCurrencyNoPermission", "You need the following permission to use this command: 'medievaleconomy.createcurrency'");
        medievalEconomy.getConfig().addDefault("createCurrencyNoRunFromConsole", "You can't run this command from the console!");
        medievalEconomy.getConfig().addDefault("configReloadedText", "Config reloaded!");
        medievalEconomy.getConfig().addDefault("reloadNoPermission", "You need the following permission to use this command: 'medievaleconomy.reload'");
        medievalEconomy.getConfig().addDefault("withdrawUsageText", "Usage: /withdraw (whole number)");
        medievalEconomy.getConfig().addDefault("withdrawPositiveText", "Number must be positive!");
        medievalEconomy.getConfig().addDefault("withdrawTextStart", "You open your coinpurse and take out ");
        medievalEconomy.getConfig().addDefault("withdrawTextEnd", " coins.");
        medievalEconomy.getConfig().addDefault("withdrawNotEnoughCoins", "You don't have that many coins in your coinpurse!");
        medievalEconomy.getConfig().addDefault("withdrawNotEnoughSpace", "You don't have enough space in your inventory for that many coins!");
        medievalEconomy.getConfig().addDefault("withdrawNoPermission", "Sorry! In order to use this command, you need the permission " + "'medievaleconomy.withdraw'");
        medievalEconomy.getConfig().addDefault("deathMessage", "Your coinpurse feels lighter than it was.");
        medievalEconomy.getConfig().addDefault("storageSaveError", "An error occurred while saving coinpurse record filenames.");
        medievalEconomy.getConfig().addDefault("storageLoadError", "Error loading the coinpurse records!");
        medievalEconomy.getConfig().addDefault("titleSeparator", true);
        medievalEconomy.getConfig().addDefault("currencyItemName", "Gold Coin");
        medievalEconomy.getConfig().addDefault("currencyItemLoreLineOne", "The currency of the Continent.");
        medievalEconomy.getConfig().addDefault("currencyItemLoreLineTwo", "Best kept in a coinpurse.");
        medievalEconomy.getConfig().addDefault("currencyItemLoreLineThree", "useful commands: /balance /deposit /withdraw");
        medievalEconomy.getConfig().addDefault("compatibilityText", "[ALERT] Old save folder name (pre v3.2) detected. Updating for compatibility.");
        medievalEconomy.getConfig().options().copyDefaults(true);
        medievalEconomy.saveConfig();
    }

}
