const {SlashCommandBuilder} = require('@discordjs/builders');
const {addSubcommands} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('move')
        .setDescription('Move an entity')
        .addSubcommand(subcommand =>
            subcommand
                .setName('character')
                .setDescription('Move a roleplay character')
                .addStringOption(option =>
                    option.setName('end-region')
                        .setDescription('The destination of the character')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('army-or-company')
                .setDescription('Move an army or trading/armed company')
                .addStringOption(option =>
                    option.setName('army-or-company-name')
                        .setDescription('The army\'s/company\'s name')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('destination-region')
                        .setDescription('The destination of the army/company')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('food-type')
                        .setDescription('The food used to pay the movement')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('move', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};