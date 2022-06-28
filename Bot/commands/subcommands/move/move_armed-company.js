const {capitalizeFirstLetters} = require("../../../utils/utilities");
const { MessageEmbed } = require('discord.js');
const {MOVE} = require('../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('armed-company-name').toLowerCase());
        const food=capitalizeFirstLetters(interaction.options.getString('food-type').toLowerCase());
        const start=interaction.options.getInteger('start-region');
        const destination=interaction.options.getInteger('destination-region');
        const replyEmbed = new MessageEmbed()
                                .setTitle(`Move armed company`)
                                .setColor('RED')
                                .setDescription(`${name} moved from ${start} to ${destination}, using ${food} for payment.`)
                                .setThumbnail(MOVE)
                                .setTimestamp()
        await interaction.deferReply();
        await interaction.editReply({embeds: [replyEmbed]});
    },
};