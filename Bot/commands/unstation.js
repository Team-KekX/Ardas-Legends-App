const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName("unstation")
        .setDescription("Unstation armies or companies from claimbuilds")
        .addSubcommand(subcommand =>
            subcommand
                .setName("army-or-company")
                .setDescription("Unstation army or company from a claimbuild")
                .addStringOption(option =>
                    option.setName("name")
                        .setDescription("Name of the army or company you want to unstation")
                        .setRequired(true)
                )
        ),

    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('unstation', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
}