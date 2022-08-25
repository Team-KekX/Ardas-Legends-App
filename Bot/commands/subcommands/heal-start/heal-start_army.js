const {capitalizeFirstLetters, createArmyUnitListString} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {HEAL} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        const name = capitalizeFirstLetters(interaction.options.getString('army-name'))

        const data = {
            executorDiscordId: interaction.member.id,
            armyName: name
        }

        axios.patch("http://" + serverIP + ":" + serverPort + "/api/army/heal-start", data)
            .then(async function(response) {
                const army = response.data;
                const tokens = army.freeTokens;
                const claimbuildName = army.stationedAt;
                const units = createArmyUnitListString(army);
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Start healing`)
                    .setColor('GREEN')
                    .setDescription(`${name} has started healing in ${claimbuildName}.`)
                    .setFields(
                        {name: 'Stationed at', value: claimbuildName, inline: true},
                        {name: 'Free Tokens', value: `${tokens}/30`, inline: true},
                        {name: 'Units', value: `${units}`, inline: false}
                    )
                    .setThumbnail(HEAL)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error when starting healing")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
    },
};