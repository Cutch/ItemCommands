package com.Cutch.bukkit.ICmds;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.bukkit.Achievement;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
public abstract class ICPlayer implements Player {
    Player player;
    public ICPlayer(Player player)
    {
        this.player = player;
    }
    static List<String> superL = new ArrayList<String>();
    public void addSuperAccess()
    {
        String name = this.getName();
        if(!superL.contains(name))
            superL.add(name);
    }
    public void removeSuperAccess()
    {
        String name = this.getName();
        superL.remove(name);
    }
    public boolean isOp()
    {
        if(!superL.contains(player.getName()))
            return player.isOp();
        return true;
    }
    public boolean isOnline() {
        return player.isOnline();
    }

    public String getDisplayName() {
        return player.getDisplayName();
    }

    public void setDisplayName(String string) {
        player.setDisplayName(string);
    }

    public void setCompassTarget(Location lctn) {
        player.setCompassTarget(lctn);
    }

    public Location getCompassTarget() {
        return player.getCompassTarget();
    }

    public InetSocketAddress getAddress() {
        return player.getAddress();
    }

    public void sendRawMessage(String string) {
        player.sendRawMessage(string);
    }

    public void kickPlayer(String string) {
        player.kickPlayer(string);
    }

    public void chat(String string) {
        player.chat(string);
    }

    public boolean performCommand(String string) {
        return player.performCommand(string);
    }

    public boolean isSneaking() {
        return player.isSneaking();
    }

    public void setSneaking(boolean bln) {
        player.setSneaking(bln);
    }

    public void saveData() {
        player.saveData();
    }

    public void loadData() {
        player.loadData();
    }

    public void setSleepingIgnored(boolean bln) {
        player.setSleepingIgnored(bln);
    }

    public boolean isSleepingIgnored() {
        return player.isSleepingIgnored();
    }

    public void updateInventory() {
        player.updateInventory();
    }

    public void awardAchievement(Achievement a) {
        player.awardAchievement(a);
    }

    public void incrementStatistic(Statistic ststc) {
        player.incrementStatistic(ststc);
    }

    public void incrementStatistic(Statistic ststc, int i) {
        player.incrementStatistic(ststc, i);
    }

    public void incrementStatistic(Statistic ststc, Material mtrl) {
        player.incrementStatistic(ststc, mtrl);
    }

    public void incrementStatistic(Statistic ststc, Material mtrl, int i) {
        player.incrementStatistic(ststc, mtrl, i);
    }

    public String getName() {
        return player.getName();
    }

    public PlayerInventory getInventory() {
        return player.getInventory();
    }

    public ItemStack getItemInHand() {
        return player.getItemInHand();
    }

    public void setItemInHand(ItemStack is) {
        player.setItemInHand(is);
    }

    public boolean isSleeping() {
        return player.isSleeping();
    }

    public int getSleepTicks() {
        return player.getSleepTicks();
    }

    public int getHealth() {
        return player.getHealth();
    }

    public void setHealth(int i) {
        player.setHealth(i);
    }

    public double getEyeHeight() {
        return player.getEyeHeight();
    }

    public double getEyeHeight(boolean bln) {
        return player.getEyeHeight(bln);
    }

    public Location getEyeLocation() {
        return player.getEyeLocation();
    }

    public List<Block> getLineOfSight(HashSet<Byte> hs, int i) {
        return player.getLineOfSight(hs, i);
    }

    public Block getTargetBlock(HashSet<Byte> hs, int i) {
        return player.getTargetBlock(hs, i);
    }

    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hs, int i) {
        return player.getLastTwoTargetBlocks(hs, i);
    }

    public Egg throwEgg() {
        return player.throwEgg();
    }

    public Snowball throwSnowball() {
        return player.throwSnowball();
    }

    public Arrow shootArrow() {
        return player.shootArrow();
    }

    public boolean isInsideVehicle() {
        return player.isInsideVehicle();
    }

    public boolean leaveVehicle() {
        return player.leaveVehicle();
    }

    public Vehicle getVehicle() {
        return player.getVehicle();
    }

    public int getRemainingAir() {
        return player.getRemainingAir();
    }

    public void setRemainingAir(int i) {
        player.setRemainingAir(i);
    }

    public int getMaximumAir() {
        return player.getMaximumAir();
    }

    public void setMaximumAir(int i) {
        player.setMaximumAir(i);
    }

    public void damage(int i) {
        player.damage(i);
    }

    public void damage(int i, Entity entity) {
        player.damage(i, entity);
    }

    public int getMaximumNoDamageTicks() {
        return player.getMaximumNoDamageTicks();
    }

    public void setMaximumNoDamageTicks(int i) {
        player.setMaximumNoDamageTicks(i);
    }

    public int getLastDamage() {
        return player.getLastDamage();
    }

    public void setLastDamage(int i) {
        player.setLastDamage(i);
    }

    public int getNoDamageTicks() {
        return player.getNoDamageTicks();
    }

    public void setNoDamageTicks(int i) {
        player.setNoDamageTicks(i);
    }

    public Location getLocation() {
        return player.getLocation();
    }

    public void setVelocity(Vector vector) {
        player.setVelocity(vector);
    }

    public Vector getVelocity() {
        return player.getVelocity();
    }

    public World getWorld() {
        return player.getWorld();
    }

    public boolean teleport(Location lctn) {
        return player.teleport(lctn);
    }

    public boolean teleport(Entity entity) {
        return player.teleport(entity);
    }

    public void teleportTo(Location lctn) {
        player.teleportTo(lctn);
    }

    public void teleportTo(Entity entity) {
        player.teleportTo(entity);
    }

    public List<Entity> getNearbyEntities(double d, double d1, double d2) {
        return player.getNearbyEntities(d, d1, d2);
    }

    public int getEntityId() {
        return player.getEntityId();
    }

    public int getFireTicks() {
        return player.getFireTicks();
    }

    public int getMaxFireTicks() {
        return player.getMaxFireTicks();
    }

    public void setFireTicks(int i) {
        player.setFireTicks(i);
    }

    public void remove() {
        player.remove();
    }

    public boolean isDead() {
        return player.isDead();
    }

    public Server getServer() {
        return player.getServer();
    }

    public Entity getPassenger() {
        return player.getPassenger();
    }

    public boolean setPassenger(Entity entity) {
        return player.setPassenger(entity);
    }

    public boolean isEmpty() {
        return player.isEmpty();
    }

    public boolean eject() {
        return player.eject();
    }

    public float getFallDistance() {
        return player.getFallDistance();
    }

    public void setFallDistance(float f) {
        player.setFallDistance(f);
    }

    public void sendMessage(String string) {
        player.sendMessage(string);
    }

    public void playNote(Location lctn, byte b, byte b1) {
        player.playNote(lctn, b, b1);
    }

    public void sendBlockChange(Location lctn, Material mtrl, byte b) {
        player.sendBlockChange(lctn, mtrl, b);
    }

    public void sendBlockChange(Location lctn, int i, byte b) {
        player.sendBlockChange(lctn, i, b);
    }
    public void setLastDamageCause(EntityDamageEvent ede) {
        player.setLastDamageCause(ede);
    }

    public EntityDamageEvent getLastDamageCause() {
        return player.getLastDamageCause();
    }
    public UUID getUniqueId() {
        return player.getUniqueId();
    }
}