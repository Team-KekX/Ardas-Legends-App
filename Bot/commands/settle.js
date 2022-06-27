const {SlashCommandBuilder} = require("@discordjs/builders");
const fs = require("fs");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('settle')
        .setDescription('Settle a trader or armed company to take different actions.')
        .addSubcommand(subcommand =>
            subcommand
                .setName('trader')
                .setDescription('Settle a trader in a claimbuild')
                .addStringOption(option =>
                    option.setName('trader-name')
                        .setDescription('The name of the trading company')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the claimbuild to settle in')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('armed-company')
                .setDescription('Settle an armed company in a claimbuild. Stations the army at the same time.')
                .addStringOption(option =>
                    option.setName('trader-name')
                        .setDescription('The name of the armed company')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the claimbuild to settle in')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('army')
                .setDescription('Settle an army in a claimbuild.')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the claimbuild to settle in')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('character')
                .setDescription('Settle a character in a claimbuild.')
                .addStringOption(option =>
                    option.setName('character-name')
                        .setDescription('The name of the roleplay character')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the claimbuild to settle in')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        addSubcommands('settle', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};