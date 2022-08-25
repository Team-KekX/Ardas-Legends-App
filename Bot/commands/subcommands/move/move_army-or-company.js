const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {MOVE} = require('../../../configs/embed_thumbnails.json');
const axios = require("axios");
const {serverIP, serverPort} = require("../../../configs/config.json");

module.exports = {
    // TO BE EXPANDED
    async execute(interaction) {
        const armyName = capitalizeFirstLetters(interaction.options.getString('army-or-company-name').toLowerCase());
        const food = capitalizeFirstLetters(interaction.options.getString('food-type').toLowerCase());
        const destination = interaction.options.getString('destination-region').toUpperCase();

        const data = {
            executorDiscordId: interaction.member.id,
            armyName: armyName,
            toRegion: destination
        }

        axios.post(`http://${serverIP}:${serverPort}/api/movement/move-army-or-company`, data)
            .then(async function (response) {
                var movement = response.data;
                var path = movement.path.path.join(' -> ');
                var replyEmbed = new MessageEmbed()
                    .setTitle(`Army ${armyName} started moving!`)
                    .setDescription(`The Army '${armyName}' started moving towards region ${destination}!`)
                    .setColor("GREEN")
                    .addFields(
                        {name: "Duration", value:movement.path.cost.toString() + " days", inline: true  },
                        {name: "Payment food", value:food, inline: true  },
                        {name: "Path", value:path, inline: false  },
                    )
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to move army!")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })

    },
};