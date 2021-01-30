package economysystem.Subsystems;

import economysystem.Objects.Coinpurse;
import economysystem.MedievalEconomy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UtilitySubsystem {

    MedievalEconomy medievalEconomy = null;

    public UtilitySubsystem(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    public void addCurrencyToInventory(Player player, int amount) {
        // if player's inventory has space
        if (!(player.getInventory().firstEmpty() == -1)) {
            player.getInventory().addItem(getCurrency(amount));
            player.sendMessage(ChatColor.GREEN + "" + amount + " currency created.");
        }
        else { // player's inventory is full
            player.sendMessage(ChatColor.RED + "Inventory full.");
        }
    }

    public void removeCurrencyFromInventory(Player player, int amount) {
        player.getInventory().removeItem(getCurrency(amount));
    }

    public ItemStack getCurrency(int amount) {
        ItemStack currencyItem = new ItemStack(Material.GOLD_NUGGET, amount);
        ItemMeta meta = currencyItem.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + medievalEconomy.getConfig().getString("currencyItemName"));
        List<String> lore = new ArrayList<String>();
        if (medievalEconomy.getConfig().getBoolean("titleSeparator")) {
            lore.add("");
        }
        lore.add(ChatColor.GOLD + "" + ChatColor.ITALIC + medievalEconomy.getConfig().getString("currencyItemLoreLineOne"));
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + medievalEconomy.getConfig().getString("currencyItemLoreLineTwo"));
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + medievalEconomy.getConfig().getString("currencyItemLoreLineThree"));

        meta.setLore(lore);
        currencyItem.setItemMeta(meta);

        return currencyItem;
    }

    public void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.AQUA + "/econ help - Show a helpful list of commands.");
        if (player.hasPermission("medievaleconomy.createcurrency")) {
            player.sendMessage(ChatColor.AQUA + "/econ createcurrency # - Bring more currency into the world.");
        }
    }

    public boolean hasCoinpurse(UUID uuid) {
        for (Coinpurse purse : medievalEconomy.coinpurses) {
            if (purse.getPlayerUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public Coinpurse getPlayersCoinPurse(UUID uuid) {
        for (Coinpurse purse : medievalEconomy.coinpurses) {
            if (purse.getPlayerUUID().equals(uuid)) {
                return purse;
            }
        }
        return null;
    }

    public void ensureSmoothTransitionBetweenVersions() {
        // this piece of code is to ensure that saves don't become broken when updating to v0.7 from a previous version
        File saveFolder = new File("./plugins/Medieval-Economy/");
        if (saveFolder.exists()) {
            System.out.println(medievalEconomy.getConfig().getString("compatibilityText"));

            File newSaveFolder = new File("./plugins/MedievalEconomy/");
            if (!newSaveFolder.exists()) {
                // rename directory
                saveFolder.renameTo(newSaveFolder);

                // delete old folder
                File oldFolder = new File("./plugins/Medieval-Economy");
                deleteLegacyFiles(oldFolder);

                // load in old saves and save them with new format
                medievalEconomy.storage.legacyLoadCoinpurses();
                medievalEconomy.storage.save();
            }

        }
    }

    // Recursive file delete from https://www.baeldung.com/java-delete-directory
    boolean deleteLegacyFiles(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteLegacyFiles(file);
            }
        }
        if (directoryToBeDeleted.getAbsolutePath().contains("config.yml")) {
            return true;
        }
        return directoryToBeDeleted.delete();
    }
}
