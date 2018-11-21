package net.runelite.client.plugins.easyscape;


import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import javax.inject.Inject;

@PluginDescriptor(
        name = "Easyscape",
        description = "Easyscape.",
        tags = {"Easyscape"},
        enabledByDefault = false
)

@Slf4j
public class EasyScapePlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private EasyScapePluginConfig config;

    @Provides
    EasyScapePluginConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(EasyScapePluginConfig.class);
    }

    @Override
    public void startUp() {
        log.debug("Dongle Started.");
    }

    @Override
    public void shutDown() {
        log.debug("Dongle Stopped.");
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {

        final String option = Text.removeTags(event.getOption()).toLowerCase();
        final String target = Text.removeTags(event.getTarget()).toLowerCase();

        if (config.getSwapShop()) {
            for (String s : config.getSwappedItems().split(",")) {
                s = s.trim();
                if (target.equalsIgnoreCase(s)) {
                    swap("Buy 50", "Value", target);
                }
            }
        }

        if (config.getSwapSmithing()) {
            if (option.equalsIgnoreCase("Smith-1")) {
                swap("Smith-All", "Smith-1", target);
            } else {
                swap("Smith-All-Sets", "Smith-1-Set", target);
            }
        }

        if (config.getSwapTanning()) {
            swap("Tan-1", "Tan-All", target);
        }

        if (option.equalsIgnoreCase("Clear-All") && target.equalsIgnoreCase("Bank Filler")) {
            swap("Clear", "Clear-All", target);
        }

        if (target.toLowerCase().contains("ardougne cloak") && config.getSwapArdougneCape()) {
            swap("Kandarin Monastery", option, target);
        }

        if (config.getSwapEssencePouch()) {
            if (target.equalsIgnoreCase("Small Pouch") || target.equalsIgnoreCase("Medium Pouch") || target.equalsIgnoreCase("Large Pouch")) {
                Widget widgetBankTitleBar = client.getWidget(WidgetInfo.BANK_TITLE_BAR);
                switch (config.getEssenceMode()) {
                    case RUNECRAFTING:
                        if (widgetBankTitleBar == null || widgetBankTitleBar.isHidden()) {
                            swap("Empty", option, target);
                        } else {
                            swap("Fill", option, target);
                        }
                        break;
                    case ESSENCE_MINING:
                        if (widgetBankTitleBar == null || widgetBankTitleBar.isHidden()) {
                            swap("Fill", option, target);
                        } else {
                            swap("Empty", option, target);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        if (config.getGamesNecklace()) {
            if (target.toLowerCase().contains("games necklace")) {
                switch (config.getGamesNecklaceMode()) {
                    case BURTHORPE:
                        swap("Burthorpe", option, target);
                        break;
                    case BARBARIAN_OUTPOST:
                        swap("Barbarian Outpost", option, target);
                        break;
                    case CORPOREAL_BEAST:
                        swap("Corporeal Beast", option, target);
                        break;
                    case TEARS_OF_GUTHIX:
                        swap("Tears of Guthix", option, target);
                        break;
                    case WINTERTODT:
                        swap("Wintertodt Camp", option, target);
                        break;
                    default:
                        break;
                }
            }
        }

        if (config.getDuelingRing()) {
            if (target.toLowerCase().contains("dueling ring")) {
                switch (config.getDuelingRingMode()) {
                    case DUEL_ARENA:
                        swap("Duel Arena", option, target);
                        break;
                    case CASTLE_WARS:
                        swap("Castle Wars", option, target);
                        break;
                    case CLAN_WARS:
                        swap("Clan Wars", option, target);
                        break;
                    default:
                        break;
                }
            }
        }
    }


    private int searchIndex(MenuEntry[] entries, String option, String target) {
        for (int i = entries.length - 1; i >= 0; i--) {
            MenuEntry entry = entries[i];
            String entryOption = Text.removeTags(entry.getOption()).toLowerCase();
            String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();

            if (entryOption.equalsIgnoreCase(option) && entryTarget.equalsIgnoreCase(target)) {
                return i;
            }
        }
        return -1;
    }

    private void swap(String optionA, String optionB, String target) {
        MenuEntry[] entries = client.getMenuEntries();

        int idxA = searchIndex(entries, optionA, target);
        int idxB = searchIndex(entries, optionB, target);

        if (idxA >= 0 && idxB >= 0) {
            MenuEntry entry = entries[idxA];
            entries[idxA] = entries[idxB];
            entries[idxB] = entry;
            client.setMenuEntries(entries);
        }
    }

}