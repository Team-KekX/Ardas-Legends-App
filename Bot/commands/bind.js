const {SlashCommandBuilder} = require("@discordjs/builders");
const fs = require("fs");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('bind')
        .setDescription('Binds a roleplay character to an entity (army, trader etc.)')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army')
                .setDescription('Binds a character to an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('character-name')
                        .setDescription('The name of the character')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('trader')
                .setDescription('Binds a character to a trading company')
                .addStringOption(option =>
                    option.setName('trader-name')
                        .setDescription('The name of the trader')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('character-name')
                        .setDescription('The name of the character')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('armed-company')
                .setDescription('Binds a character to an armed company')
                .addStringOption(option =>
                    option.setName('armed-company-name')
                        .setDescription('The name of the armed company')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('character-name')
                        .setDescription('The name of the character')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        addSubcommands('bind', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};