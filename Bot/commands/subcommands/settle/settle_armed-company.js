const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {SETTLE} = require('../../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name = capitalizeFirstLetters(interaction.options.getString('trader-name').toLowerCase());
        const claimbuild = capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
            .setTitle(`Settle armed company`)
            .setColor('RED')
            .setDescription(`${name} has now settled in ${claimbuild}. The accompanying army has been stationed.`)
            .setThumbnail(SETTLE)
            .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};