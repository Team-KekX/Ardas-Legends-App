const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('unbind')
        .setDescription('Unbinds a roleplay character to an entity (army, trader etc.)')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army-or-company')
                .setDescription('Unbinds a character from an army')
                .addStringOption(option =>
                    option.setName('army-or-company-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
                .addUserOption(option =>
                    option.setName("target-player")
                        .setDescription("The player you want to unbind, PING that discord account!")
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('unbind', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
};