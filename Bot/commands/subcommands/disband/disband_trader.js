const {capitalizeFirstLetters} = require("../../../utils/utilities");
const { MessageEmbed } = require('discord.js');

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('trader-name').toLowerCase());
        await interaction.reply(`The trading company \"${name}\" has been disbanded.`);
    },
};