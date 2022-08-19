const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('get')
        .setDescription('Different Commands that get data')
        .addSubcommand(option =>
            option
                .setName("upkeep")
                .setDescription("Shows each faction, their amount of armies and their upkeep for those armies")
        ),

    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('get', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};
