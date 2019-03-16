const React = require('react');

import { Table, Label } from 'semantic-ui-react'
import axios from "axios";
import { findIndex } from 'lodash';
import cloneDeep from 'lodash/cloneDeep';

import PinRow from './PinRow'

class PinListTab extends React.Component {

	constructor(props) {
		super(props);
		this.state = { config: {
							pins: [],
							config: []
						}
					};
		this.onPinModeChange = this.onPinModeChange.bind(this);
		this.processMessage = this.processMessage.bind(this);
		this.onPinStateChange = this.onPinStateChange.bind(this);
		
        this.webSocket = new WebSocket('ws://' + window.location.hostname + ':8080/ws');
	}

 	processMessage(msg) {
        console.log('WS message prosessing: ' + msg);
        console.log(msg);
        let message = JSON.parse(msg.data);
        console.log(message);
		if (message.eventType) {
		    if (message.eventType == 'INFO') {
                console.log(message);
                const newConfig = cloneDeep(this.state.config);
		        let idx = findIndex(newConfig.config, {address: message.address});
                newConfig.config[idx].pinState = message.state;
                if (message.mode) {
                    console.log('Updating pin mode');
                    newConfig.config[idx].pinMode = message.mode;
                }
                console.log(newConfig.config[idx]);
                this.setState({ config: newConfig });
		    }
		}
	}
	
	componentDidMount() {
	    axios.get('/api/pins')
            .then(response => {
                let newState = Object.assign({}, this.state);
                newState.config = response.data;
                this.setState(newState);
            });
		this.webSocket.onmessage = this.processMessage;

		this.webSocket.onclose = function () {
		 	alert("WebSocket connection closed") 
		};
	}

	
	onPinStateChange = (address, value) => {
	    const message = {
            eventType: 'CHANGESTATE',
            address: address,
			state: value
		};
		this.webSocket.send( JSON.stringify(message) );
	}
	
	
	onPinModeChange = (pin, newMode) => {
    	const message = {
            eventType: 'CHANGEMODE',
    	 	address: pin.address,
			name: pin.name,
    		mode: newMode,
    		pullUp: 'OFF'
		};
		this.webSocket.send( JSON.stringify(message) );
  	}

	render() {
	    let { pins } = this.state.config;
	    let { config } = this.state.config;
	    
	    if (!Array.isArray(config) || !config.length) {
	    	config = [];
	    }
	    
	    if (!Array.isArray(pins) || !pins.length) {
            return  <Label>Loading...</Label>
        } else {
            return (
             <Table celled>
			    <Table.Header>
			      <Table.Row>
			        <Table.HeaderCell>Name</Table.HeaderCell>
			        <Table.HeaderCell>Address</Table.HeaderCell>
			        <Table.HeaderCell>Mode</Table.HeaderCell>
			        <Table.HeaderCell>PullUP</Table.HeaderCell>
			        <Table.HeaderCell>State</Table.HeaderCell>
			      </Table.Row>
			    </Table.Header>

			    <Table.Body>
			     {pins.map(pin => (
			     	  <PinRow pin={pin} pinConfig={config} onPinStateChange={this.onPinStateChange}  onPinModeChange={this.onPinModeChange} />
			      	))}  
			    </Table.Body>
 			 </Table>)
        }
	}
}

export default (PinListTab);