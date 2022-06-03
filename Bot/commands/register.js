const {SlashCommandBuilder} = require('@discordjs/builders');
const {capitalizeFirstLetters} = require("../utils/utilities");
const {availableFactions} = require("../configs/config.json");

// Needs to be further implemented.
// Reaction counting is currently not implemented.
module.exports = {
    data: new SlashCommandBuilder()
        .setName('register')
        .setDescription('Register in the roleplay system')
        .addStringOption(option =>
            option.setName('ign')
                .setDescription('Your minecraft in-game name (IGN)')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('faction-name')
                .setDescription('The faction you want to join')
                .setRequired(true)),
    async execute(interaction) {
        const ign = capitalizeFirstLetters(interaction.options.getString('ign').toLowerCase());
        const faction = capitalizeFirstLetters(interaction.options.getString('faction-name').toLowerCase());

        if (!availableFactions.includes(faction)) {
            await interaction.reply({content: `${faction} is not a valid faction.`, ephemeral: true});
            await interaction.followUp({
                content: `Available factions: ${availableFactions.join(', ')}`,
                ephemeral: true
            });
        } else {
            await interaction.reply({
                content: `You were successfully registered as ${ign} in the faction ${faction}.`,
                ephemeral: true
            });
            // send to server

        }
    },
};