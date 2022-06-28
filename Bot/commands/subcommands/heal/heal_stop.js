const {capitalizeFirstLetters} = require("../../../utils/utilities");
const { MessageEmbed } = require('discord.js');
const {HEAL} = require('../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
                                .setTitle(`Stop healing`)
                                .setColor('RED')
                                .setDescription(`${name} has stopped healing.`)
                                .setThumbnail(HEAL)
                                .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};