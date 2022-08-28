const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands, saveExecute} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('update')
        .setDescription('Updates information about an entity')
        .addSubcommand(subcommand =>
            subcommand
                .setName('faction')
                .setDescription('Update your faction')
                .addStringOption(option =>
                    option.setName('faction-name')
                        .setDescription('The name of the faction')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('ign')
                .setDescription('Update your minecraft IGN')
                .addStringOption(option =>
                    option.setName('ign')
                        .setDescription('Your new IGN')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('discord-id')
                .setDescription('Update the discord ID of a player')
                .addStringOption(option =>
                    option.setName('old-discord-id')
                        .setDescription('The player\'s old discord ID')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('new-discord-id')
                        .setDescription('The player\'s new discord ID')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar-name')
                .setDescription('Update the name of your Roleplay Character')
                .addStringOption(option =>
                    option.setName('new-name')
                        .setDescription('The new name of your rp char')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar-title')
                .setDescription('Update the title of your Roleplay Character')
                .addStringOption(option =>
                    option.setName('new-title')
                        .setDescription('The new title of your rp char')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar-gear')
                .setDescription('Update the gear of your Roleplay Character')
                .addStringOption(option =>
                    option.setName('new-gear')
                        .setDescription('The new gear of your rp char')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar-pvp')
                .setDescription('Update the PvP status of your Roleplay Character')
                .addBooleanOption(option =>
                    option.setName('new-pvp')
                        .setDescription('The new PvP status of your rp char')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName("free-tokens")
                .setDescription("Sets the amount of free tokens an army has")
                .addStringOption(option =>
                    option
                        .setName("army-name")
                        .setDescription("The name of the army of which the free tokens will be changed")
                        .setRequired(true)
                )
                .addIntegerOption(option =>
                    option
                        .setName("tokens")
                        .setDescription("The new amount of free tokens which will be set")
                        .setRequired(true)
                )
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName("paid")
                .setDescription("Updates the payment attribute of an army or company to true")
                .addStringOption(option =>
                    option
                        .setName("name")
                        .setDescription("Name of the army or company that should receive a payment")
                        .setRequired(true)
                )
                .addBooleanOption(option =>
                    option
                        .setName("is-paid")
                        .setDescription("Sets if the army is paid")
                        .setRequired(true)
                )
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName("claimbuild-faction")
                .setDescription("Updates the controlling faction of a claimbuild")
                .addStringOption(option =>
                    option
                        .setName("claimbuild")
                        .setDescription("Name of the claimbuild where the owner is to be changed")
                        .setRequired(true)
                )
                .addStringOption(option =>
                    option
                        .setName("faction")
                        .setDescription("Name of the faction that should control the claimbuild")
                        .setRequired(true)
                )
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('update', true);
        const toExecute = commands[interaction.options.getSubcommand()];
        saveExecute(toExecute, interaction);
    },
};
