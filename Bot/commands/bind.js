const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('bind')
        .setDescription('Binds a roleplay character to an entity (army, trader etc.)')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army-or-company')
                .setDescription('Binds a character to an army or trading/armed company')
                .addStringOption(option =>
                    option.setName('army-or-company-name')
                        .setDescription('The name of the army/company')
                        .setRequired(true))
                .addUserOption(option =>
                    option.setName("target-player")
                        .setDescription("Player that gets bound to the army, PING that discord account!")
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('bind', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
};