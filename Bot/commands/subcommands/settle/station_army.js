const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {SETTLE} = require('../../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name = capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild = capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
            .setTitle(`Station army`)
            .setColor('RED')
            .setDescription(`${name} is now stationed at ${claimbuild}.`)
            .setThumbnail(SETTLE)
            .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};