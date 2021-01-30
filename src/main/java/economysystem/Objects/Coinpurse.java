package economysystem.Objects;

import economysystem.MedievalEconomy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class Coinpurse {

    MedievalEconomy medievalEconomy = null;

    public Coinpurse(MedievalEconomy plugin) {
        medievalEconomy = plugin;
    }

    private UUID uuid = null;
    private int numCoins = 0;

    public void setPlayerUUID(UUID id) {
        uuid = id;
    }

    public UUID getPlayerUUID() {
        return uuid;
    }

    public void setCoins(int i) {
        numCoins = i;
    }

    public int getCoins() {
        return numCoins;
    }

    public void addCoins(int num) {
        numCoins = numCoins + num;
    }

    public boolean removeCoins(int num) {
        if ((numCoins - num) >= 0) {
            numCoins = numCoins - num;
            return true;
        }
        return false;
    }

    public boolean containsAtLeast(int num) {
        if (numCoins >= num) {
            return true;
        }
        return false;
    }

    public void save() {
        try {
            File saveFolder = new File("./plugins/MedievalEconomy/");
            if (!saveFolder.exists()) {
                saveFolder.mkdir();
            }
            File saveFolder2 = new File("./plugins/MedievalEconomy/Coinpurse-Records/");
            if (!saveFolder2.exists()) {
                saveFolder2.mkdir();
            }
            File saveFile = new File("./plugins/MedievalEconomy/Coinpurse-Records/" + uuid + ".txt");
            saveFile.createNewFile();

            FileWriter saveWriter = new FileWriter("./plugins/MedievalEconomy/Coinpurse-Records/" + uuid + ".txt");

            // actual saving takes place here
            saveWriter.write(uuid.toString() + "\n");
            saveWriter.write(numCoins + "\n");

            saveWriter.close();

        } catch (IOException e) {
            System.out.println(medievalEconomy.getConfig().getString("coinpurseSaveErrorText"));
        }
    }

    public void load(String filename) {
        try {
            File loadFile = new File("./plugins/MedievalEconomy/Coinpurse-Records/" + filename);
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            if (loadReader.hasNextLine()) {
                uuid = UUID.fromString(loadReader.nextLine());
            }

            if (loadReader.hasNextLine()) {
                numCoins = Integer.parseInt(loadReader.nextLine());
            }

            loadReader.close();
            System.out.println(filename + " successfully loaded.");
        } catch (FileNotFoundException e) {
            System.out.println(medievalEconomy.getConfig().getString("coinpurseLoadErrorText") + filename);
        }
    }

    public void legacyLoad(String filename) {
        try {
            File loadFile = new File("./plugins/MedievalEconomy/Coinpurse-Records/" + filename);
            Scanner loadReader = new Scanner(loadFile);

            // actual loading
            if (loadReader.hasNextLine()) {
                String playerName = loadReader.nextLine();

                uuid = medievalEconomy.findUUIDBasedOnPlayerName(playerName);
            }

            if (loadReader.hasNextLine()) {
                numCoins = Integer.parseInt(loadReader.nextLine());
            }

            loadReader.close();
            loadFile.delete();
        } catch (FileNotFoundException e) {
            System.out.println(medievalEconomy.getConfig().getString("coinpurseLoadErrorText") + filename);
        }
    }
}
