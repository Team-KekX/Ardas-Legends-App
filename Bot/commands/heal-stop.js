const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('heal-stop')
        .setDescription('Stops healing an entity (army, character, company, ...)')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army')
                .setDescription('Stop healing of an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('character')
                .setDescription('Stop the healing of your roleplay character')
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('heal-stop', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
};