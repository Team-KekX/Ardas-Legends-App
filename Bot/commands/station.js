const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('station')
        .setDescription('Station a trader or armed company to take different actions.')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army-or-company')
                .setDescription('Station an army or company at a claimbuild.')
                .addStringOption(option =>
                    option.setName('name')
                        .setDescription('The name of the army or company')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the claimbuild to station at')
                        .setRequired(true))
        ),

    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('station', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};