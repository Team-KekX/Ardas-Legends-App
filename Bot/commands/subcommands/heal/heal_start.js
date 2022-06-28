const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {HEAL} = require('../../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name = capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild_name = capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const tokens = interaction.options.getInteger('tokens');
        const replyEmbed = new MessageEmbed()
            .setTitle(`Start healing`)
            .setColor('RED')
            .setDescription(`${name} has started healing ${tokens} in ${claimbuild_name}.`)
            .setThumbnail(HEAL)
            .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};