const {capitalizeFirstLetters} = require("../../../utils/utilities");
const { MessageEmbed } = require('discord.js');
const {BIND} = require('../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('trader-name').toLowerCase());
        const character=capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
                                .setTitle(`Bind trading company`)
                                .setColor('RED')
                                .setDescription(`${character} has been bound to the trading company ${name}.`)
                                .setThumbnail(BIND)
                                .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};