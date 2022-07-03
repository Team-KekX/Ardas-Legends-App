const {capitalizeFirstLetters, isMemberStaff} = require("../../../../utils/utilities");
const {MessageEmbed} = require("discord.js");
const {ADMIN} = require("../../../../configs/embed_thumbnails.json");

module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }
        const oldId = capitalizeFirstLetters(interaction.options.getString('old-discord-id').toLowerCase());
        const newId = capitalizeFirstLetters(interaction.options.getString('new-discord-id').toLowerCase());
        // send to server
        const replyEmbed = new MessageEmbed()
            .setTitle(`Update Discord ID`)
            .setColor('NAVY')
            .setDescription(`Updated discord ID of player from ${oldId} to ${newId}.`)
            .setThumbnail(ADMIN)
            .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};
