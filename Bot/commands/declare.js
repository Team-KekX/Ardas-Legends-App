const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('declare')
        .setDescription('Declare a battle or war')
        .addSubcommand(subcommand =>
            subcommand
                .setName('battle')
                .setDescription('Declare a battle')
                .addStringOption(option =>
                    option.setName('target')
                        .setDescription('The name of the claimbuild, or \'fieldbattle\' for a field battle')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('attacker-list')
                        .setDescription('The list of attacking armies. Separate using a comma (\',\').')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('defender-list')
                        .setDescription('The list of defending armies.')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('war-camp-coordinates')
                        .setDescription('The coordinates of the war camp')
                        .setRequired(false))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('war')
                .setDescription('Declare war to a faction')
                .addStringOption(option =>
                    option
                        .setName('attacker-faction')
                        .setDescription('Your faction name')
                        .setRequired(true))
                .addStringOption(option =>
                    option
                        .setName('defender-faction')
                        .setDescription('The faction you are declaring war to')
                        .setRequired(true)),
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('peace')
                .setDescription('Declare peace to a faction you are at war with')
                .addStringOption(option =>
                    option
                        .setName('attacker-faction')
                        .setDescription('Your faction name')
                        .setRequired(true))
                .addStringOption(option =>
                    option
                        .setName('defender-faction')
                        .setDescription('The faction you are declaring war to')
                        .setRequired(true)),
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('declare', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};