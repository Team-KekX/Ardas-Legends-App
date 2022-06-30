const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('heal')
        .setDescription('Starts or stops healing an army')
        .addSubcommand(subcommand =>
            subcommand
                .setName('start')
                .setDescription('Start healing an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the character')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('tokens')
                        .setDescription('How many tokens to heal the army')
                        .setRequired(true)
                        .setMaxValue(30)
                        .setMinValue(0))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('stop')
                .setDescription('Stop the healing of an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('heal', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};