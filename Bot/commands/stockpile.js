const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('stockpile')
        .setDescription('Base Command for all stockpile commands')
        .addSubcommand(subcommand =>
            subcommand
                .setName('add')
                .setDescription(`Add food stacks to a faction's stockpile`)
                .addStringOption(option =>
                    option.setName('faction-name')
                        .setDescription('The name of the faction')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option
                        .setName("amount-to-add")
                        .setDescription("Amount that is to be added")
                        .setRequired(true)
                )
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('remove')
                .setDescription(`Remove food stacks from a faction's stockpile`)
                .addStringOption(option =>
                    option.setName('faction-name')
                        .setDescription('The name of the faction')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option
                        .setName("amount-to-remove")
                        .setDescription("Amount that is to be removed")
                        .setRequired(true)
                )
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('info')
                .setDescription(`Information about a faction's stockpile`)
                .addStringOption(option =>
                    option.setName('faction-name')
                        .setDescription('The name of the faction')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('stockpile', true);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
};
