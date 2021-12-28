package me.deecaad.weaponmechanics.weapon.reload.ammo;

import me.deecaad.core.file.Serializer;
import me.deecaad.weaponmechanics.mechanics.CastData;
import me.deecaad.weaponmechanics.mechanics.Mechanics;
import me.deecaad.weaponmechanics.utils.CustomTag;
import me.deecaad.weaponmechanics.weapon.trigger.Trigger;
import me.deecaad.weaponmechanics.wrappers.IPlayerWrapper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

public class Ammo implements Serializer<Ammo> {

    private Mechanics outOfAmmoMechanics;
    private boolean ammoTypeSwitchAutomaticWhenOutOfAmmo;
    private Trigger ammoTypeSwitchTrigger;
    private Mechanics ammoTypeSwitchMechanics;
    private List<IAmmoType> ammoTypes;

    public String getCurrentAmmoName(ItemStack weaponStack) {
        return ammoTypes.get(CustomTag.AMMO_TYPE_INDEX.getInteger(weaponStack)).getAmmoName();
    }

    public int getCurrentAmmoIndex(ItemStack weaponStack) {
        return CustomTag.AMMO_TYPE_INDEX.getInteger(weaponStack);
    }

    public void setCurrentAmmoIndex(ItemStack weaponStack, int index) {
        CustomTag.AMMO_TYPE_INDEX.setInteger(weaponStack, index);
    }

    public boolean hasAmmo(String weaponTitle, ItemStack weaponStack, IPlayerWrapper playerWrapper) {
        int index = getCurrentAmmoIndex(weaponStack);
        if (ammoTypes.get(index).hasAmmo(playerWrapper)) {
            return true;
        }
        if (!ammoTypeSwitchAutomaticWhenOutOfAmmo || ammoTypes.size() == 1) return false;

        // Check from top to bottom for other ammo types
        for (int i = 0; i < ammoTypes.size(); ++i) {
            if (i == index) continue; // Don't try checking for that ammo type anymore

            if (!ammoTypes.get(i).hasAmmo(playerWrapper)) continue;

            // Update the index automatically to use this new one
            setCurrentAmmoIndex(weaponStack, i);
            ammoTypeSwitchMechanics.use(new CastData(playerWrapper, weaponTitle, weaponStack));
            return true;
        }
        return false;
    }

    public int removeAmmo(String weaponTitle, ItemStack weaponStack, IPlayerWrapper playerWrapper, int amount) {
        int index = getCurrentAmmoIndex(weaponStack);
        int removeAmount = ammoTypes.get(index).removeAmmo(playerWrapper, amount);
        if (removeAmount != 0) return removeAmount;

        if (!ammoTypeSwitchAutomaticWhenOutOfAmmo || ammoTypes.size() ==  1) return 0;

        // Check from top to bottom for other ammo types
        for (int i = 0; i < ammoTypes.size(); ++i) {
            if (i == index) continue; // Don't try checking for that ammo type anymore

            removeAmount = ammoTypes.get(i).removeAmmo(playerWrapper, amount);
            if (removeAmount == 0) continue;

            // Update the index automatically to use this new one
            setCurrentAmmoIndex(weaponStack, i);
            ammoTypeSwitchMechanics.use(new CastData(playerWrapper, weaponTitle, weaponStack));
            return removeAmount;
        }

        return 0;
    }

    public void giveAmmo(ItemStack weaponStack, IPlayerWrapper playerWrapper, int amount) {
        ammoTypes.get(getCurrentAmmoIndex(weaponStack)).giveAmmo(playerWrapper, amount);

        // No need to try switching since this will simply give amount of current ammo back
    }

    public int getMaximumAmmo(ItemStack weaponStack, IPlayerWrapper playerWrapper) {
        // No need to try switching since this will simply maximum amount of current ammo
        return ammoTypes.get(getCurrentAmmoIndex(weaponStack)).getMaximumAmmo(playerWrapper);
    }

    @Override
    public String getKeyword() {
        return "Ammo";
    }

    @Override
    public Ammo serialize(File file, ConfigurationSection configurationSection, String path) {
        return null;
    }
}
