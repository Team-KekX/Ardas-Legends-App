const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('disband')
        .setDescription('Disbands an entity (trader, army etc.)')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army-or-company')
                .setDescription('Disbands an army or company')
                .addStringOption(option =>
                    option.setName('name')
                        .setDescription('The name of the army / company')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('armed-company')
                .setDescription('Breaks the armed company into separate trader and army. Character gets unbound.')
                .addStringOption(option =>
                    option.setName('armed-company-name')
                        .setDescription('The name of the armed company')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('disband', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
};