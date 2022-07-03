const {capitalizeFirstLetters, isMemberStaff} = require("../../../../utils/utilities");
const {MessageEmbed} = require("discord.js");
const {ADMIN} = require("../../../../configs/embed_thumbnails.json");


module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }
        const discordId = capitalizeFirstLetters(interaction.options.getString('discord-id').toLowerCase());
        // send to server
        const replyEmbed = new MessageEmbed()
            .setTitle(`Delete player`)
            .setColor('NAVY')
            .setDescription(`Deleted player with discord ID: ${discordId}.`)
            .setThumbnail(ADMIN)
            .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    }
};