const {capitalizeFirstLetters} = require("../../../utils/utilities");
const { MessageEmbed } = require('discord.js');
const {CREATE} = require('../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('trader-name').toLowerCase());
        const claimbuild=capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const replyEmbed = new MessageEmbed()
                                .setTitle(`Create trading company`)
                                .setColor('RED')
                                .setDescription(`The trading company ${name} has been created at ${claimbuild}.`)
                                .setThumbnail(CREATE)
                                .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};