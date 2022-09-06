const {capitalizeFirstLetters, createPathString, createCostString} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {MOVE_CHARACTER} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require('axios');

module.exports = {
    async execute(interaction) {

        const destination = interaction.options.getString('end-region');

        const data = {
            discordId: interaction.member.id,
            toRegion: destination
        }

        console.log("Starting request")
        axios.post('http://'+serverIP+':'+serverPort+'/api/movement/move-char', data)
            .then(async function(response) {
                console.log("Request handled")
                console.log(response.data);

                const name = response.data.player.rpChar.name;
                const start = response.data.startRegionId;
                const path = createPathString(response.data.path);
                const cost = createCostString(response.data.cost);

                const startMsg = `${name} started his movement to region ${destination}.`;

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Move character`)
                    .setColor('GREEN')
                    .setDescription(startMsg)
                    .setFields(
                        {name: "Route", value: path, inline:false},
                        {name: "Duration", value: cost, inline:false},
                    )
                    .setThumbnail(MOVE_CHARACTER)
                    .setTimestamp()

                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                console.log(error)
                //error occurred
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to move character!")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]})
            })


    },
};