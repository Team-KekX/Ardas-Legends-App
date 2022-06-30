const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {UNBIND} = require('../../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name = capitalizeFirstLetters(interaction.options.getString('trader-name').toLowerCase());
        const character = capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
            .setTitle(`Unbind trading company`)
            .setColor('RED')
            .setDescription(`${character} has been unbound from the trading company ${name}.`)
            .setThumbnail(UNBIND)
            .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};