const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require("discord.js");
const {UPDATE_IGN} = require("../../../configs/embed_thumbnails.json");

module.exports = {
    async execute(interaction) {
        const ign = capitalizeFirstLetters(interaction.options.getString('ign').toLowerCase());
        await interaction.deferReply();
        // send to server and edit reply
        const replyEmbed = new MessageEmbed()
            .setTitle(`Update IGN`)
            .setColor('GREEN')
            .setDescription(`You successfully updated your ign to ${ign}.`)
            .setThumbnail(UPDATE_IGN)
            .setTimestamp()
        await interaction.editReply({embeds: [replyEmbed], ephemeral: true});
    },
};