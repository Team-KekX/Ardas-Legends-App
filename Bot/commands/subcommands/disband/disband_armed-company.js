const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {DISBAND} = require('../../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name = capitalizeFirstLetters(interaction.options.getString('armed-company-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
            .setTitle(`Disband armed company`)
            .setColor('RED')
            .setDescription(`The armed company \"${name}\" has been disbanded. Now the army and traders are
                                separated.`)
            .setThumbnail(DISBAND)
            .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};