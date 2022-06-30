const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {CREATE} = require('../../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name = capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild = capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const units = capitalizeFirstLetters(interaction.options.getString('unit-list').toLowerCase());
        const replyEmbed = new MessageEmbed()
            .setTitle(`Create army`)
            .setColor('RED')
            .setDescription(`The army ${name} comprised of ${units}, has been created at ${claimbuild}.`)
            .setThumbnail(CREATE)
            .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};