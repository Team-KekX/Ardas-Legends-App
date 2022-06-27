const {SlashCommandBuilder} = require("@discordjs/builders");
const fs = require("fs");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('create')
        .setDescription('Creates an entity (army, trader etc.)')
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
                    option.setName('unit-list')
                        .setDescription('The list of units in the army')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('trader')
                .setDescription('Creates a trading company')
                .addStringOption(option =>
                    option.setName('trader-name')
                        .setDescription('The name of the trader')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the originating claimbuild')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('armed-company')
                .setDescription('Creates an armed company')
                .addStringOption(option =>
                    option.setName('armed-company-name')
                        .setDescription('The name of the armed company')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('trader-name')
                        .setDescription('The name of the trader')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('character-name')
                        .setDescription('The name of the character bound to army/trader')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        addSubcommands('create', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};