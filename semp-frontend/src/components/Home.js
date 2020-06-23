import React from 'react';
import styled from 'styled-components';
import FetchWrapper from './server/FetchWrapper';

const GridWrapper = styled.div`
  display: grid;
  grid-gap: 10px;
  margin-top: 1em;
  margin-left: 12em;
  margin-right: 12em;
  grid-template-columns: repeat(12, 1fr);
  grid-auto-rows: minmax(25px, auto);
`;

class Home extends React.Component {
  cotacao = null;

  render() {
    let dataDiv = null;
    let valorDiv = null;

    FetchWrapper.doGet('/cotacao').then(response => {
      if (response.data) {
        dataDiv = <p>Data: { response.data }</p>
       }
      if (response.valor) {
        valorDiv = <p>Valor: { response.valor }</p>
      }
    })
    .catch(error => {
      dataDiv = <p>Erro ao carregar a cotação</p>
      valorDiv = <p>{ error }</p>
    });

    return (
      <GridWrapper>
        <p>This is a paragraph and I am writing on the home page</p>
        <p>This is another paragraph, hi hey hello whatsup yo</p>
        { dataDiv }
        { valorDiv }
      </GridWrapper>
  
    );
  }
}

export default Home;
