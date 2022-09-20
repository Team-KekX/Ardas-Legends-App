package com.ardaslegends.data.presentation.discord.utils;

import java.util.Objects;

public enum FactionBanners {

    ANGMAR("https://cdn.discordapp.com/attachments/1021694169846140959/1021694225689100288/Angmar_Banner.PNG.png"),
    BLUE_MOUNTAINS("https://cdn.discordapp.com/attachments/1021694169846140959/1021694308799229973/Blue_Mountains_Banner.PNG.png"),
    BREE("https://cdn.discordapp.com/attachments/1021694169846140959/1021694381717213184/Bree-land_Banner.png"),
    DALE("https://cdn.discordapp.com/attachments/1021694169846140959/1021694547836813312/Dale_Banner.PNG.png"),
    DOL_AMROTH("https://cdn.discordapp.com/attachments/1021694169846140959/1021694648114221088/Dol_Amroth_Banner.PNG.png"),
    DOL_GULDUR("https://cdn.discordapp.com/attachments/1021694169846140959/1021694718075228160/Dol_Guldur_Banner.PNG.png"),
    DORWINION("https://cdn.discordapp.com/attachments/1021694169846140959/1021694778141835304/Dorwinion_Banner.PNG.png"),
    DUNLAND("https://cdn.discordapp.com/attachments/1021694169846140959/1021694814003146764/Dunland_Banner.PNG.png"),
    DURINS_FOLK("https://cdn.discordapp.com/attachments/1021694169846140959/1021700601844875294/Dwarf_Banner.PNG.png"),
    RHUDEL("https://cdn.discordapp.com/attachments/1021694169846140959/1021700693318438932/Easterling_Banner.png"),
    LOTHLORIEN("https://cdn.discordapp.com/attachments/1021694169846140959/1021700718375215104/Galadhrim_Banner.PNG.png"),
    GONDOR("https://cdn.discordapp.com/attachments/1021694169846140959/1021700747718574120/Gondor_Banner.PNG.png"),
    GULF_OF_HARAD("https://cdn.discordapp.com/attachments/1021694169846140959/1021700850814566430/Gulf_of_Harad_Banner.PNG.png"),
    GUNDABAD("https://cdn.discordapp.com/attachments/1021694169846140959/1021700866023112754/Gundabad_Banner.PNG.png"),
    HALF_TROLL("https://cdn.discordapp.com/attachments/1021694169846140959/1021700896909967380/Half-troll_Banner.PNG.png"),
    LINDON("https://cdn.discordapp.com/attachments/1021694169846140959/1021700936923623484/High_Elf_Banner.PNG.png"),
    HOBBIT("https://cdn.discordapp.com/attachments/1021694169846140959/1021700944385277972/Hobbit_Banner.PNG.png"),
    MORDOR("https://cdn.discordapp.com/attachments/1021694169846140959/1021700959694508142/Mordor_Banner.PNG.png"),
    MORWAITH("https://cdn.discordapp.com/attachments/1021694169846140959/1021701061452513350/Moredain_Banner.PNG.png"),
    HARNENNOR("https://cdn.discordapp.com/attachments/1021694169846140959/1021701068893196338/Near_Harad_Banner.PNG.png"),
    DUNEDAIN("https://cdn.discordapp.com/attachments/1021694169846140959/1021701100476309544/Ranger_Banner.PNG.png"),
    RIVENDELL("https://cdn.discordapp.com/attachments/1021694169846140959/1021701251651608627/Rivendell_Banner.png"),
    ROHAN("https://cdn.discordapp.com/attachments/1021694169846140959/1021701256814809130/Rohan_Banner.PNG.png"),
    NOMADS("https://cdn.discordapp.com/attachments/1021694169846140959/1021701339182538812/Southron_Nomads_Banner.PNG_1.png"),
    TAURETHRIM("https://cdn.discordapp.com/attachments/1021694169846140959/1021701993787576320/Tauredain_Banner.PNG.png"),
    UMBAR("https://cdn.discordapp.com/attachments/1021694169846140959/1021702356359970887/Umbar_Banner.PNG.png"),
    ISENGARD("https://cdn.discordapp.com/attachments/1021694169846140959/1021702402518290462/Uruk_Banner.PNG.png"),
    WANDERER("https://cdn.discordapp.com/attachments/1021694169846140959/1021702509275926598/Wanderer_Banner.png"),
    WOOD_ELVES("https://cdn.discordapp.com/attachments/1021694169846140959/1021702534731137096/Wood-Elf_Banner.PNG.png");


    private final String url;
    FactionBanners(String url) {
       this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public static String getBannerUrl(String factionName) {
        Objects.requireNonNull(factionName, "FactionBanner: factionName must not be null!");

        return switch (factionName) {
            case "Angmar" -> ANGMAR.getUrl();
            case "Bree" -> BREE.getUrl();
            case "Dale" -> DALE.getUrl();
            case "Dol Amroth" -> DOL_AMROTH.getUrl();
            case "Dol Guldur" -> DOL_GULDUR.getUrl();
            case "Dorwinion" -> DORWINION.getUrl();
            case "Dunland" -> DUNLAND.getUrl();
            case "Durin's Folk" -> DURINS_FOLK.getUrl();
            case "Ered Luin" -> BLUE_MOUNTAINS.getUrl();
            case "Gondor" -> GONDOR.getUrl();
            case "Gulf of Harad" -> GULF_OF_HARAD.getUrl();
            case "Gundabad" -> GUNDABAD.getUrl();
            case "Half-Trolls" -> HALF_TROLL.getUrl();
            case "Harnennor" -> HARNENNOR.getUrl();
            case "Hobbits" -> HOBBIT.getUrl();
            case "Isengard" -> ISENGARD.getUrl();
            case "Lindon" -> LINDON.getUrl();
            case "Lothlórien" -> LOTHLORIEN.getUrl();
            case "Mordor" -> MORDOR.getUrl();
            case "Morwaith" -> MORWAITH.getUrl();
            case "Nomads" -> NOMADS.getUrl();
            case "Rangers of the North" -> DUNEDAIN.getUrl();
            case "Rhúdel" -> RHUDEL.getUrl();
            case "Rivendell" -> RIVENDELL.getUrl();
            case "Rohan" -> ROHAN.getUrl();
            case "Southron Coast" -> NOMADS.getUrl();
            case "Taurethrim" -> TAURETHRIM.getUrl();
            case "Umbar" -> UMBAR.getUrl();
            case "Wanderer" -> WANDERER.getUrl();
            case "Woodland Realm" -> WOOD_ELVES.getUrl();
            default -> throw new RuntimeException("Banner for faction '%s' has not been found".formatted(factionName));
        };
    }
}
