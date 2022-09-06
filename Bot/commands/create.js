const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('create')
        .setDescription('Creates an entity (RpChar, army, trader etc.)')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army')
                .setDescription('Creates an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the originating claimbuild')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('units')
                        .setDescription('The list of units in the army - example syntax = Gondor Archer:5-Mordor Orc:3')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar')
                .setDescription('Creates a Roleplay Character')
                .addUserOption(option =>
                    option
                        .setName("target-player")
                        .setDescription("The player you want to create an RPChar for")
                        .setRequired(true)
                )
                .addStringOption(option =>
                    option.setName('name')
                        .setDescription("Character's name")
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('title')
                        .setDescription("Character's title")
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('gear')
                        .setDescription("Character's gear")
                        .setRequired(true))
                .addBooleanOption(option =>
                    option.setName('pvp')
                        .setDescription('Should the character participate in PvP?')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('claimbuild')
                .setDescription('Creates a claimbuild')
                .addStringOption(option =>
                    option.setName('name')
                        .setDescription('Name of the claimbuild')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('region')
                        .setDescription('The id of the region the claimbuild is located in')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('type')
                        .setDescription('Claimbuild type, e.g. Hamlet or Capital. You can look up the types on the data spreadsheet')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('faction')
                        .setDescription('The faction that owns this claimbuild')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('x')
                        .setDescription('The x coordinate of the build')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('y')
                        .setDescription('The y coordinate of the build')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('z')
                        .setDescription('The z coordinate of the build')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('traders')
                        .setDescription('Trader NPCs present at this build. The bot does not handle this input.')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('siege')
                        .setDescription('Siege present at this building. Seperate the sieges with ,')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('number-of-houses')
                        .setDescription('Number of houses in the build. E.g. 14 small houses. The bot does not handle this input.')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('built-by')
                        .setDescription('Players who built the cb. Seperate player with -   Example: Luktronic-mirak441')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('production-sites')
                        .setDescription('Production Sites in the cb. Example: Fishing Lodge:Salmon:2-Mine:Iron:5')
                        .setRequired(false))
                .addStringOption(option =>
                    option.setName('special-buildings')
                        .setDescription('Seperate the buildings with -   Example: House of Healing-Embassy')
                        .setRequired(false))

        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('create', true);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
};