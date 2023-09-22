package me.deecaad.weaponmechanics.wrappers;

import me.deecaad.weaponmechanics.WeaponMechanics;
import me.deecaad.weaponmechanics.weapon.shoot.recoil.RecoilTask;
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponReloadCancelEvent;
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponReloadCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HandData {

    private final EntityWrapper entityWrapper;
    private final boolean mainhand;

    private int fullAutoTask;
    private int burstTask;
    private final Map<String,Long> lastShotTimes=new HashMap<>();
    private long lastScopeTime;
    private long lastEquipTime;
    private double spreadChange;
    private RecoilTask recoilTask;
    private long lastMeleeTime;
    private long lastMeleeMissTime;

    private long reloadStart;
    private final Set<Integer> reloadTasks = new HashSet<>();
    private ItemStack reloadWeaponStack;
    private String reloadWeaponTitle;

    private ZoomData zoomData;
    private final Set<Integer> firearmActionTasks = new HashSet<>();

    private String currentWeaponTitle;

    public HandData(EntityWrapper entityWrapper, boolean mainhand) {
        this.entityWrapper = entityWrapper;
        this.mainhand = mainhand;
    }

    public EntityWrapper getEntityWrapper() {
        return entityWrapper;
    }

    public boolean isMainhand() {
        return mainhand;
    }

    /**
     * Cancels following things
     * - Full auto
     * - Burst
     * - Reload tasks
     * - Shoot firearm action task
     * - Zooming
     *
     * Does not cancel recoil task.
     */
    public void cancelTasks() {
        cancelTasks(false);
    }

    /**
     * @param trySkinUpdate whether to also try to update skin
     */
    public void cancelTasks(boolean trySkinUpdate) {
        if (fullAutoTask != 0) {
            Bukkit.getScheduler().cancelTask(fullAutoTask);
            fullAutoTask = 0;
        }
        if (burstTask != 0) {
            Bukkit.getScheduler().cancelTask(burstTask);
            burstTask = 0;
        }
        stopReloadingTasks();
        stopFirearmActionTasks();
        getZoomData().ifZoomingForceZoomOut();

        if (!trySkinUpdate) return;

        // Try to update skin in given hand
        LivingEntity livingEntity = entityWrapper.getEntity();
        if (livingEntity.getType() == EntityType.PLAYER && ((Player) livingEntity).getGameMode() == GameMode.SPECTATOR) return;

        EntityEquipment entityEquipment = livingEntity.getEquipment();
        if (entityEquipment == null) return;

        ItemStack weaponStack;
        String weaponTitle;

        if (mainhand) {
            weaponStack = entityEquipment.getItemInMainHand();
        } else {
            weaponStack = entityEquipment.getItemInOffHand();
        }

        weaponTitle = WeaponMechanics.getWeaponHandler().getInfoHandler().getWeaponTitle(weaponStack, false);

        if (weaponTitle == null) return;

        WeaponMechanics.getWeaponHandler().getSkinHandler().tryUse(entityWrapper, weaponTitle, weaponStack, mainhand ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND);
    }

    public boolean isUsingFullAuto() {
        return fullAutoTask != 0;
    }

    public void setFullAutoTask(int fullAutoTask) {
        this.fullAutoTask = fullAutoTask;
    }

    public boolean isUsingBurst() {
        return burstTask != 0;
    }

    public void setBurstTask(int burstTask) {
        this.burstTask = burstTask;
    }

    public void setLastShotTime(String weaponTitle,long lastShotTime) {
        this.lastShotTimes.put(weaponTitle,lastShotTime);
    }

    public long getLastShotTime(String weaponTitle) {
        return lastShotTimes.getOrDefault(weaponTitle,0L);
    }

    public long getLastScopeTime() {
        return lastScopeTime;
    }

    public void setLastScopeTime(long lastScopeTime) {
        this.lastScopeTime = lastScopeTime;
    }

    public long getLastReloadTime() {
        return reloadStart;
    }

    public long getLastEquipTime() {
        return lastEquipTime;
    }

    public void setLastEquipTime(long lastEquipTime) {
        this.lastEquipTime = lastEquipTime;
    }

    public double getSpreadChange() {
        return spreadChange;
    }

    public void setSpreadChange(double spreadChange) {
        this.spreadChange = spreadChange;
    }

    public RecoilTask getRecoilTask() {
        return recoilTask;
    }

    public void setRecoilTask(RecoilTask recoilTask) {
        this.recoilTask = recoilTask;
    }

    public long getLastMeleeTime() {
        return lastMeleeTime;
    }

    public void setLastMeleeTime(long lastMeleeTime) {
        this.lastMeleeTime = lastMeleeTime;
    }

    public long getLastMeleeMissTime() {
        return lastMeleeMissTime;
    }

    public void setLastMeleeMissTime(long lastMeleeMissTime) {
        this.lastMeleeMissTime = lastMeleeMissTime;
    }

    public void addReloadTask(int reloadTask) {
        if (this.reloadTasks.isEmpty()) {
            // Reload is starting
            reloadStart = System.currentTimeMillis();
        }
        this.reloadTasks.add(reloadTask);
    }

    public void addReloadTasks(int... reloadTasks) {
        if (this.reloadTasks.isEmpty()) {
            reloadStart = System.currentTimeMillis();
        }
        for (int i : reloadTasks) {
            this.reloadTasks.add(i);
        }
    }

    public boolean isReloading() {
        return !reloadTasks.isEmpty();
    }

    public int getReloadElapsedTime() {
        return (int) ((System.currentTimeMillis() - reloadStart) / 50);
    }

    public void finishReload() {
        if (!reloadTasks.isEmpty()) {
            for (int task : reloadTasks) {
                Bukkit.getScheduler().cancelTask(task);
            }
            reloadTasks.clear();

            Bukkit.getPluginManager().callEvent(new WeaponReloadCompleteEvent(reloadWeaponTitle, reloadWeaponStack, entityWrapper.getEntity(), mainhand ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND));

            reloadWeaponStack = null;
            reloadWeaponTitle = null;
        }
    }

    public void stopReloadingTasks() {
        if (!reloadTasks.isEmpty()) {
            for (int task : reloadTasks) {
                Bukkit.getScheduler().cancelTask(task);
            }
            reloadTasks.clear();

            Bukkit.getPluginManager().callEvent(new WeaponReloadCancelEvent(reloadWeaponTitle, reloadWeaponStack, entityWrapper.getEntity(), mainhand ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND, getReloadElapsedTime()));

            reloadWeaponStack = null;
            reloadWeaponTitle = null;
        }
    }

    public void setReloadData(String weaponTitle, ItemStack weaponStack) {
        this.reloadWeaponTitle = weaponTitle;
        this.reloadWeaponStack = weaponStack;
    }

    public ZoomData getZoomData() {
        return zoomData == null ? zoomData = new ZoomData(this) : zoomData;
    }

    /**
     * Only used with shoot firearm actions.
     * Reload firearm actions use addReloadTask()
     */
    public void addFirearmActionTask(int firearmTask) {
        firearmActionTasks.add(firearmTask);
    }

    /**
     * Only used with shoot firearm actions
     * Reload firearm actions use addReloadTask()
     */
    public void addFirearmActionTasks(int... firearmTask) {
        for (int i : firearmTask) {
            firearmActionTasks.add(i);
        }
    }

    /**
     * Only used with shoot firearm actions
     * Reload firearm actions use addReloadTask()
     */
    public boolean hasRunningFirearmAction() {
        return !firearmActionTasks.isEmpty();
    }

    /**
     * Only used with shoot firearm actions
     * Reload firearm actions use addReloadTask()
     */
    public void stopFirearmActionTasks() {
        if (!firearmActionTasks.isEmpty()) {
            for (int task : firearmActionTasks) {
                Bukkit.getScheduler().cancelTask(task);
            }
            firearmActionTasks.clear();
        }
    }

    public String getCurrentWeaponTitle() {
        return currentWeaponTitle;
    }

    public void setCurrentWeaponTitle(String currentWeaponTitle) {
        this.currentWeaponTitle = currentWeaponTitle;
    }
}