const {capitalizeFirstLetters} = require("../../../utils/utilities");
const { MessageEmbed } = require('discord.js');
const {BIND} = require('../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const character=capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
                                .setTitle(`Bind army`)
                                .setColor('RED')
                                .setDescription(`${character} has been bound to the army ${name}.`)
                                .setThumbnail(BIND)
                                .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};