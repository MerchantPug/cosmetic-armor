package io.github.apace100.cosmetic_armor;

import dev.emi.trinkets.api.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CosmeticArmor implements ModInitializer {

	public static final String MODID = "cosmetic-armor";

	public static final Tag<Item> BLACKLIST = TagRegistry.item(id("blacklist"));
	public static final Tag<Item> ALWAYS_VISIBLE = TagRegistry.item(id("always_visible"));

	@Override
	public void onInitialize() {
		for(int i = 0; i < 4; i++) {
			EquipmentSlot slot = EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i);
			TrinketsApi.registerTrinketPredicate(id(slot.getName()), (stack, slotReference, entity) -> {
				if(BLACKLIST.contains(stack.getItem())) {
					return TriState.FALSE;
				}
				if(MobEntity.getPreferredEquipmentSlot(stack) == slot) {
					return TriState.TRUE;
				}
				return TriState.DEFAULT;
			});
		}
	}

	public static ItemStack getCosmeticArmor(LivingEntity entity, EquipmentSlot slot) {
		Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
		if(component.isPresent()) {
			List<Pair<SlotReference, ItemStack>> list = component.get().getEquipped(stack -> MobEntity.getPreferredEquipmentSlot(stack) == slot);
			for(Pair<SlotReference, ItemStack> equipped : list) {
				SlotType slotType = equipped.getLeft().inventory().getSlotType();
				if(!slotType.getName().equals("cosmetic")) {
					continue;
				}
				if(!slotType.getGroup().equalsIgnoreCase(slot.getName())) {
					continue;
				}
				return equipped.getRight();
			}
		}
		return ItemStack.EMPTY;
	}

	private static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
}
