package com.javaminecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Chopper extends JavaPlugin {
    public static final Logger log = Logger.getLogger("Minecraft");
    Player me;
    World world;
    Location spot;
    
    // for trees found inside the 40x40x40 cube
    ArrayList<Loc> trees = new ArrayList<>();
    
    //for all blocks searched inside the cube
    ArrayList<Loc> searched = new ArrayList<>();
    
    // Simple form of Location Class
    class Loc {
        int x, y, z;
        
        Loc(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    
   @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        me = (Player) sender;
        world = me.getWorld();
        spot = me.getLocation();
        
        if (label.equalsIgnoreCase("chop")) {
            if (sender instanceof Player) {
                executeCommand();
            }
            return true;
        }
        return false;
        
    }
    private void executeCommand() {
        int spotX = spot.getBlockX();
        int spotY = spot.getBlockY();
        int spotZ = spot.getBlockZ();
        
        //loop through all possible x, y, z locations in the cube
        for (int x = spotX - 10; x < spotX + 11; x++) {
            for (int z = spotZ - 10; z < spotZ + 11; z++) {
                for (int y = spotY - 10; y < spotY + 11; y++) {
                    Location searchLoc = new Location(world, x, y, z);
                    Block here = searchLoc.getBlock();
                    Loc loc = new Loc(x, y, z);
                    searched.add(loc);
                    
                    if (here.getType() == Material.LOG || here.getType() == Material.LOG_2) {
                        here.setType(Material.AIR);
                        trees.add(loc);
                    }
                }
            }
        }
        for (Loc loc : trees) {
            chopAdjacentTrees(loc);
        }
        log.info("Chopping trees down");
        
    }
    private void chopAdjacentTrees(Loc chopLoc) {
        Location spot = new Location(world, chopLoc.x, chopLoc.y, chopLoc.z);
        int spotX = spot.getBlockX();
        int spotY = spot.getBlockY();
        int spotZ = spot.getBlockZ();
        
        for (int x = spotX - 1; x < spotX + 2; x++) {
            for (int y = spotY - 1; y < spotY + 2; y++) {
                for (int z = spotZ - 1; z < spotZ + 2; y++) {
                    Loc loc = new Loc(x, y, z);
                    if (searched.contains(loc)) continue;
                    // if it hasn't been searched
                    searched.add(loc);
                    Location searchLoc = new Location(world, x, y, z);
                    Block here = searchLoc.getBlock();
                    if (here.getType() == Material.LOG || here.getType() == Material.LOG_2) {
                        
                        here.setType(Material.AIR);
                        chopAdjacentTrees(loc);
                    }
                }
            }
        }
    }
}