const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('injure')
        .setDescription('Starts healing an entity (army, character, company, ...)')
        .addSubcommand(subcommand =>
            subcommand
                .setName('character')
                .setDescription('Injure your roleplay character.')
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('injure', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
};