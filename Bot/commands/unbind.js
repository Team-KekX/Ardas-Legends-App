const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('unbind')
        .setDescription('Unbinds a roleplay character to an entity (army, trader etc.)')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army')
                .setDescription('Unbinds a character from an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
                .addUserOption(option =>
                    option.setName("target-player")
                        .setDescription("The player you want to unbind, PING that discord account!")
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('trader')
                .setDescription('Unbinds a character to a trading company')
                .addStringOption(option =>
                    option.setName('trader-name')
                        .setDescription('The name of the trader')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('character-name')
                        .setDescription('The name of the character')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('armed-company')
                .setDescription('Unbinds a character to an armed company')
                .addStringOption(option =>
                    option.setName('armed-company-name')
                        .setDescription('The name of the armed company')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('character-name')
                        .setDescription('The name of the character')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('unbind', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};