const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands} = require("../utils/utilities");

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
                        .setDescription('The list of units in the army - example syntax = Gondorian Ranger:5-Gondorian Soldier:5')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar')
                .setDescription('Creates a Roleplay Character')
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
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('create', true);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};