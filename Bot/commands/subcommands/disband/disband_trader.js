const {capitalizeFirstLetters} = require("../../../utils/utilities");
const { MessageEmbed } = require('discord.js');
const {DISBAND} = require('../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('trader-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
                                .setTitle(`Disband trading company`)
                                .setColor('RED')
                                .setDescription(`The trading company \"${name}\" has been disbanded.`)
                                .setThumbnail(DISBAND)
                                .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};