const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('get')
        .setDescription('Different Commands that get data')
        .addSubcommand(option =>
            option
                .setName("upkeep")
                .setDescription("Shows each faction, their amount of armies and their upkeep for those armies")
                .addStringOption(option =>
                    option
                        .setName("faction-name")
                        .setDescription("The name of the faction")
                        .setRequired(true)
                )
        )
        .addSubcommand(option =>
            option
                .setName("unpaid-armies-or-companies")
                .setDescription("Shows the 10 oldest unpaid armies or companies")
        ),

    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('get', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
};
