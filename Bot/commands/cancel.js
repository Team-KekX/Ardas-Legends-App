const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('cancel-move')
        .setDescription('Cancels a move. Beware that the army/character will stay at their current region.')
        .addSubcommand(subcommand =>
            subcommand
                .setName('character')
                .setDescription('Cancels a roleplay character movement')
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('army')
                .setDescription('Cancels an army movement')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('Name of the army')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('cancel', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
};