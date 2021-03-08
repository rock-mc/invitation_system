package com.rock_mc.invitation_system;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Blacklist {
    public HashSet<Prisoner> playerList;
    private String filePath;

    public Blacklist(String loadFile) throws Exception {
        filePath = loadFile;

        Path p = Path.of(loadFile);
        if(p.toFile().exists()) {
            String fileString = Files.readString(p);
            playerList = new Gson().fromJson(fileString, new TypeToken<HashSet<Prisoner>>(){}.getType());
        }
        else{
            playerList = new HashSet<>();
        }
    }

    public Blacklist() {
        filePath = null;
        playerList = new HashSet<>();
    }

    public Prisoner getPrisoner(UUID playerUid){
        Prisoner result = null;
        for(Prisoner prisoner : playerList){
            if(prisoner.uuid.equals(playerUid)){
                result = prisoner;
                break;
            }
        }
        return result;
    }

    public void add(UUID playerUid, int day, int hour, int min, int sec) throws IOException {

        Prisoner currentPrisoner = getPrisoner(playerUid);
        if(currentPrisoner != null){
            currentPrisoner.setExpiryTime(day, hour, min, sec);
        }
        else{
            Prisoner prisoner = new Prisoner(playerUid, day, hour, min, sec);
            playerList.add(prisoner);
        }
        save();
    }

    public void save() throws IOException {
        if (filePath == null) {
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json_str = gson.toJson(playerList);

        Util.writeFile(filePath, json_str);
    }

    public void remove(UUID playerUid) throws IOException {

        Prisoner currentPrisoner = null;
        for(Prisoner prisoner : playerList){
            if(prisoner.uuid.equals(playerUid)){
                currentPrisoner = prisoner;
                break;
            }
        }
        if (currentPrisoner == null){
            return;
        }

        playerList.remove(currentPrisoner);
        save();
    }

    public boolean contains(UUID playerUid) {
        return getPrisoner(playerUid) != null;
    }
}
