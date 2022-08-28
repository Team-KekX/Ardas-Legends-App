const {SlashCommandBuilder} = require("@discordjs/builders");
const axios = require("axios");
const {serverIP, serverPort} = require("../../../../configs/config.json");
const {MessageEmbed} = require("discord.js");
const {isMemberStaff, createProductionSiteString, capitalizeFirstLetters} = require("../../../../utils/utilities");
const {CREATE} = require("../../../../configs/embed_thumbnails.json");

module.exports = {
    async execute(interaction) {

        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }


        //Not doing capitalizeFirstLetter here so people have more freedom in naming the cb
        const name = interaction.options.getString('name');
        const region = interaction.options.getString('region');
        const type = interaction.options.getString('type');
        const faction = interaction.options.getString('faction');
        const x = interaction.options.getInteger('x');
        const y = interaction.options.getInteger('y');
        const z = interaction.options.getInteger('z');
        const prodSites = interaction.options.getString('production-sites');
        const specialBuildings = interaction.options.getString('special-buildings');
        const traders = interaction.options.getString('traders');
        const siege = interaction.options.getString('siege');
        const numberOfHouses = interaction.options.getString('number-of-houses');
        const builtBy = interaction.options.getString('built-by');


        const data = {
            name: name,
            regionId: region,
            type: type,
            faction: faction,
            xCoord: x,
            yCoord: y,
            zCoord: z,
            productionSites: prodSites,
            specialBuildings: specialBuildings,
            traders: traders,
            siege: siege,
            numberOfHouses: numberOfHouses,
            builtBy: builtBy
        }

        axios.patch(`http://${serverIP}:${serverPort}/api/claimbuild/update`, data)
            .then(async function (response) {
                var claimbuild = response.data;
                console.log(claimbuild)
                const type = capitalizeFirstLetters(claimbuild.type.toLowerCase())
                let specialBuildings = capitalizeFirstLetters(claimbuild.specialBuildings.join("\n").toLowerCase()).replaceAll(" ", "\n").replaceAll("_", " ")

                const coords = claimbuild.coordinates;
                var replyEmbed = new MessageEmbed()
                    .setTitle(`Claimbuild ${name} was successfully updated!`)
                    .setDescription(`The claimbuild '${name}' was successfully updated!`)
                    .setColor("GREEN")
                    .setThumbnail(CREATE)
                    .addFields(
                        {name: "Name", value:name, inline: true  },
                        {name: "Faction", value:claimbuild.ownedBy.name, inline: true },
                        {name: "Region", value:claimbuild.region.id, inline: true  },
                        {name: "Type", value:type, inline: true  },
                        {name: "Production Sites", value: createProductionSiteString(claimbuild.productionSites), inline: false},
                        {name: "Special Buildings", value:specialBuildings, inline: false },
                        {name: "Traders", value:claimbuild.traders, inline: true  },
                        {name: "Siege", value:claimbuild.siege, inline: true  },
                        {name: "Houses", value:claimbuild.numberOfHouses, inline: true  },
                        {name: "Coordinates", value:`${coords.x}/${coords.y}/${coords.z}`, inline: true  },
                        {name: "Built by", value:claimbuild.builtBy.join(", "), inline: true  },
                    )
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })
            .catch(async function(error) {
                console.log(error)
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to create claimbuild!")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })

    }
}