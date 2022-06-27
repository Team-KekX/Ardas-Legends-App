const {capitalizeFirstLetters} = require("../../../utils/utilities");
const { MessageEmbed } = require('discord.js');

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('trader-name').toLowerCase());
        const claimbuild=capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        await interaction.reply(`The trading company ${name} has been created at ${claimbuild}.`);
    },
};